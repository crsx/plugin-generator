/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.xtextgen;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.crsx.pgutil.PGContext;
import net.sf.crsx.pgutil.common.Util;
import net.sf.crsx.pgutil.pggrammar.PGAlternative;
import net.sf.crsx.pgutil.pggrammar.PGArtificial;
import net.sf.crsx.pgutil.pggrammar.PGCharacters;
import net.sf.crsx.pgutil.pggrammar.PGChoice;
import net.sf.crsx.pgutil.pggrammar.PGElement;
import net.sf.crsx.pgutil.pggrammar.PGElementVisitor;
import net.sf.crsx.pgutil.pggrammar.PGGeneration;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGInline;
import net.sf.crsx.pgutil.pggrammar.PGLiteral;
import net.sf.crsx.pgutil.pggrammar.PGNonTerminal;
import net.sf.crsx.pgutil.pggrammar.PGOutlineHints;
import net.sf.crsx.pgutil.pggrammar.PGPluginGeneratorProperties;
import net.sf.crsx.pgutil.pggrammar.PGTerminal;
import net.sf.crsx.pgutil.pggrammar.PGCharacters.PGCharactersBase;

public class XtextGrammarGenerator implements PGElementVisitor{
    
    static enum Mode {
        PG_TERMINAL,
        PG_NONTERMINAL
    }
    
    protected PGContext currentContext;
    
    public XtextGrammarGenerator(){
        currentContext = null;
    }
    
    public void generateXtextGrammar(PGGrammar grammar, PrintStream os){
        currentContext = new PGContext(os);
        currentContext.setMode(PGContext.Mode.PG_NONTERMINAL);
        grammar.visit(this);
        
        grammar.setOutlineHints( currentContext.getOutlineHints() );
        currentContext = null;
    }
    
    @Override
    public void visit(PGAlternative alternative) {
        boolean needsBrackets = false;
        boolean isOptional = false;
        
        List<PGChoice> alternatives = alternative.getAlternatives();
        PrintStream printStream = currentContext.getPrintStream();
        
        if(alternatives.isEmpty())
            return;
        
        if(alternatives.size() != alternative.getPrintableCount(currentContext) ){
            isOptional = true;
        }
        
        if(alternatives.size() > 1 
                || (alternatives.size() == 1 && isOptional) 
                || alternative.getCardinality() != PGElement.Cardinality.SINGLE){
            needsBrackets = true;
        }
        
        if( needsBrackets){
            printStream.print("(");
        }
        
        boolean first = true;
        boolean tmp;
        boolean defaultUsed = false;
        for( PGChoice choice : alternatives ){
            if(!choice.isPrintable(currentContext)){
                if(defaultUsed || currentContext.isInstanceCreated())
                    continue;
                PGNonTerminal nonterminal = currentContext.getCurrentNonTerminal();
                if( nonterminal != null ){
                    if(first){
                        first = false;
                    }else{
                        printStream.println("|");
                    }
                    String classname = String.format("{fDefault%s}", Util.normalizeName(nonterminal.getName()));
                    printStream.println(classname);
                    currentContext.setCurrentNonterminalClass(Util.unquote(classname));
                    defaultUsed = true;
                }
                continue;
            }
            tmp = currentContext.isInstanceCreated();
            if(first){
                first = false;
            }else{
                printStream.println("|");
            }
            
            choice.visit(this);
            
            if(currentContext.getMode() == PGContext.Mode.PG_NONTERMINAL){
                String currentClassname = currentContext.getCurrentNonterminalClass();
                PGOutlineHints hints = currentContext.getOutlineHints();
                hints.addOutlineHint(currentClassname, choice.getPluginGeneratorProperties(), currentContext.getInstanceVarList());
            }
            if(!tmp){
                currentContext.destroyInstance();
            }
            
        }
        
        if(needsBrackets)
            printStream.print(")");
        
        switch( alternative.getCardinality() ){
        case SINGLE:
            if(isOptional){
                printStream.print("?");
            }
            break;
        case ITERATION:
            printStream.print("*");
            break;
        case OPTIONAL:
            printStream.print("?");
            break;
        case POSITIVE_ITERATION:
            printStream.print(isOptional ? "*" : "+" );
            break;
        default:
            //Do nothing
        }
    }

    @Override
    public void visit(PGCharacters characters) {
        List<PGCharacters.PGCharactersBase> elements = characters.getElements();
        PrintStream printStream = currentContext.getPrintStream();
        
        if(elements.isEmpty()){
            if( characters.isNegated() ){
                printStream.println(".");
            }
            return;
        }
        boolean first = true;
        printStream.print( characters.isNegated() ? "!(":"(");
        for( PGCharactersBase chars : elements ){
            if(first){
                first = false;
            }else{
                printStream.print("|");
            }
            printStream.print(chars.getXtextString());
        }
        printStream.print(")");
    }

    @Override
    public void visit(PGChoice choice) {
        List<PGElement> elements = choice.getElements();
        PrintStream printStream = currentContext.getPrintStream();
        
        
        int counter = 0;
        PGPluginGeneratorProperties props = choice.getPluginGeneratorProperties();
        for(PGElement element : elements ){
            if(!element.isPrintable(currentContext)){
                continue;
            }
            
            if(props != null && props.getIdx() == counter){
                switch(props.getRefMode()){
                    case ID:
                        currentContext.setNextNameReferenced(true);
                        currentContext.setReferenceLabel(props.getIdxRefLabel(), currentContext.getCurrentNonterminalClass());
                        break;
                    case REFERENCE:
                        currentContext.setNextReferenceLabel(props.getIdxRefLabel());
                        break;
                    case NONE:
                    default:
                        //Do nothing
                }
            }
            element.visit(this);

            if(!(element instanceof PGAlternative)){
                switch(element.getCardinality() ){
                    case ITERATION:
                        printStream.print("*");
                        break;
                    case POSITIVE_ITERATION:
                        printStream.print("+");
                        break;
                    case OPTIONAL:
                        printStream.print("?");
                        break;
                    default:
                }
            }
            printStream.print(" ");
            if(element instanceof PGTerminal 
                    || element instanceof PGNonTerminal
                    || element instanceof PGLiteral){
                ++counter;
            }
        }
    }

    @Override
    public void visit(PGGeneration generation) {
        //Do nothing
    }

    @Override
    public void visit(PGInline inline) {
        if(inline.getUsage().wasConverted() && !currentContext.isInstanceCreated() && !currentContext.isStringRule()){
            PrintStream printStream = currentContext.getPrintStream();
            String classname = String.format("{f%s}", Util.normalizeName(inline.getInlineStr()));
            printStream.print(classname);
            currentContext.createInstance();
            currentContext.setCurrentNonterminalClass(Util.unquote(classname));
        }
    }

    protected void printXtextTerminalLiteral(PrintStream printStream,PGLiteral literal){
        String escaped = literal.getString().replaceAll("'", "\\\\'");
        printStream.print(String.format("'%s'", escaped));
    }
    
    protected void printXtextNonTerminalLiteral(PrintStream printStream,PGContext context,PGLiteral literal){
        if( literal.getUsage().wasConverted() && !context.isInstanceCreated() && !currentContext.isStringRule()){
            String strLiteral = literal.getString();
            String objName = Util.normalizeName(strLiteral.substring(1, strLiteral.length()-1));
            String classname = String.format("{f%s}", objName);
            printStream.print(classname);
            context.createInstance();
            context.setCurrentNonterminalClass(Util.unquote(classname));
        }
        printStream.print(literal.getString());
        context.insertVarStub();
    }
    
    @Override
    public void visit(PGLiteral literal) {
        PrintStream printStream = currentContext.getPrintStream();
        switch(currentContext.getMode()){
        case PG_NONTERMINAL:
            printXtextNonTerminalLiteral(printStream,currentContext,literal);
            break;
        case PG_TERMINAL:
            printXtextTerminalLiteral(printStream,literal);
            break;
        default:
            //Do nothing
        }
    }

    @Override
    public void visit(PGNonTerminal nonterminal) {
        PrintStream printStream = currentContext.getPrintStream();
        
        if(!currentContext.isStringRule()){
            printStream.print(currentContext.getVarForProduction(nonterminal));
            if( nonterminal.getCardinality() == PGElement.Cardinality.ITERATION 
                    || nonterminal.getCardinality() == PGElement.Cardinality.POSITIVE_ITERATION ){
                printStream.print("+=");
            }else{
                printStream.print("=");
            }
        }
        printRightAssignSign(printStream, nonterminal.getName());
        //printStream.print(nonterminal.getName());
    }
    
    protected String getNormalizedTerminalName(PGTerminal terminal){
        if("comment".equalsIgnoreCase(terminal.getName())){
            return "ML_COMMENT";
        }else{
            String normalizedName = Util.normalizeName(terminal.getName().toUpperCase());
            return String.format("TERMINAL_%s",normalizedName);
        }
    }
    
    private void printRightAssignSign(PrintStream printStream,String terminalOrNonterminalName){
        String referenceLabel = currentContext.getNextReferenceLabel();
        if(referenceLabel == null){
            printStream.print(terminalOrNonterminalName);
        }else{
            String referencedObjectName = currentContext.getReferencedObjectName(referenceLabel);
            if(referencedObjectName == null){
                String errorMsg = String.format("Referenced label \"%s\" was not defined prior to usage",referenceLabel);
                Logger.getGlobal().log(Level.SEVERE, errorMsg);
                System.exit(-1);
            }
            String s = String.format("[%s|%s]", referencedObjectName,terminalOrNonterminalName);
            printStream.print(s);
            currentContext.setNextReferenceLabel(null);
        }
    }

    @Override
    public void visit(PGTerminal terminal) {
        PrintStream printStream = currentContext.getPrintStream();
        if(!currentContext.isStringRule()){
            if(currentContext.getMode() == PGContext.Mode.PG_TERMINAL){
                printStream.println(getNormalizedTerminalName(terminal));
                return;
            }
            printStream.print(currentContext.getVarForTerminal(terminal));
            if( terminal.getCardinality() == PGElement.Cardinality.ITERATION 
                    || terminal.getCardinality() == PGElement.Cardinality.POSITIVE_ITERATION ){
                printStream.print("+=");
            }else{
                printStream.print("=");
            }
        }
        printRightAssignSign(printStream, getNormalizedTerminalName(terminal));
        //printStream.println(getNormalizedTerminalName(terminal));
    }
    
    protected void writeTerminal(PGTerminal terminal,PrintStream printStream , boolean isFragment){
        String normalizedName = getNormalizedTerminalName(terminal);
        String fragment = isFragment ? "fragment " : "";
        printStream.print(String.format("terminal %s%s : ", fragment,normalizedName));
        terminal.getAlternative().visit(this);
        printStream.println(";");
    }
    
    @Override
    public void visit(PGGrammar grammar) {
        PrintStream printStream = currentContext.getPrintStream();
        
        String commentTerminalName = grammar.isCommentTerminalSet() ? "ML_COMMENT" : "";
        
        printStream.println(String.format("grammar %s hidden(TERMINAL_SKIP,%s)%n",
                grammar.getGrammarClassName(),commentTerminalName));
        printStream.println("import \"http://www.eclipse.org/emf/2002/Ecore\" as ecore");
        printStream.println();
        
        String namespaceURI = Util.generateNamespaceURIForQualifiedName(grammar.getGrammarClassName());
        String grammarName = grammar.getName();
        
        printStream.println(String.format("generate %s \"%s\"%n%n",grammarName,namespaceURI));
        
        for( PGNonTerminal nonTerminal : grammar.getNonTerminals() ){
            if(nonTerminal.isStringRule()){
                currentContext.setStringRule(true);
                currentContext.destroyInstance();
                currentContext.setCurrentNonTerminal(nonTerminal);
                //currentContext.setCurrentNonterminalClass(String.format("%sBase", nonTerminal.getName()));
                printStream.println(String.format("%s returns ecore::EString :",nonTerminal.getName()));
            }else{
                currentContext.setStringRule(false);
                currentContext.destroyInstance();
                currentContext.setCurrentNonTerminal(nonTerminal);
                currentContext.setCurrentNonterminalClass(String.format("%sBase", nonTerminal.getName()));
                printStream.println(nonTerminal.getName()+" returns "+nonTerminal.getName()+"Base :");
            }
            nonTerminal.getAlternatives().visit(this);
            printStream.println(";");
            printStream.println();
        }
        
        
        currentContext.setMode(PGContext.Mode.PG_TERMINAL);
        
        Collection<PGTerminal> terminalList = grammar.getTerminals();
        
        //Process terminal fragments
        for( PGTerminal terminal : terminalList ){
            if( terminal.isFragment() ){
                writeTerminal(terminal,printStream,true);
            }
        }
        
        printStream.println();
        
        //Process terminals
        for(PGTerminal terminal : terminalList ){
            if(!terminal.isFragment()){
                writeTerminal(terminal,printStream,false);
            }
        }
        
    }

    @Override
    public void visit(PGArtificial artificial) {
        PrintStream printStream = currentContext.getPrintStream();
        printStream.println(artificial.getContent());
    }

}
