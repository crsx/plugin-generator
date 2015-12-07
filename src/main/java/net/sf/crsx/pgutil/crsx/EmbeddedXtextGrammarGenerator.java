/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.crsx;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import net.sf.crsx.pgutil.Constants;
import net.sf.crsx.pgutil.PGContext;
import net.sf.crsx.pgutil.common.Util;
import net.sf.crsx.pgutil.pggrammar.PGElement;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGLiteral;
import net.sf.crsx.pgutil.pggrammar.PGNonTerminal;
import net.sf.crsx.pgutil.pggrammar.PGTerminal;
import net.sf.crsx.pgutil.xtextgen.XtextGrammarGenerator;

public class EmbeddedXtextGrammarGenerator extends XtextGrammarGenerator{

    PGGrammar grammar;
    
    public EmbeddedXtextGrammarGenerator(PGGrammar grammar){
        this.grammar = grammar;
    }
    
    protected String getEmbeddedNonTerminalName(String name){
        return String.format("%s_%s", grammar.getName().toUpperCase(),name);
    }
    
    public String getXtextGrammarRules(){
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        currentContext = new PGContext(printStream);
        currentContext.setMode(PGContext.Mode.PG_NONTERMINAL);
        
        for( PGNonTerminal nonTerminal : grammar.getNonTerminals() ){
            
            printStream.println(getEmbeddedNonTerminalName(nonTerminal.getName())+" returns "+nonTerminal.getName()+"Base :");
            currentContext.destroyInstance();
            currentContext.setCurrentNonTerminal(nonTerminal);
            currentContext.setCurrentNonterminalClass(String.format("%sBase", nonTerminal.getName()));
            nonTerminal.getAlternatives().visit(this);
            printStream.println(";");
            printStream.println();
        }
        
        String result = new String(baos.toByteArray(),java.nio.charset.StandardCharsets.UTF_8);
        grammar.setOutlineHints(currentContext.getOutlineHints());
        currentContext = null;
        return result;
    }
    
    public String getXtextTerminalRules(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        currentContext = new PGContext(printStream);
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
            if( terminal.getName().equalsIgnoreCase(Constants.PG_COMMENT_DEFAULT_TERMINAL_NAME)
                    || terminal.getName().equalsIgnoreCase(Constants.PG_WHITESPACE_DEFAULT_TERMINAL_NAME) 
                    || terminal.getName().equalsIgnoreCase(Constants.PG_EMBEDDED_DEFAULT_TERMINAL_NAME)){
                    continue;
                }
            if(!terminal.isFragment()){
                writeTerminal(terminal,printStream,false);
            }
        }
        
        
        String result = new String(baos.toByteArray(),java.nio.charset.StandardCharsets.UTF_8);
        currentContext = null;
        return result;
    }
    
    @Override
    public void visit(PGNonTerminal nonterminal) {
        PrintStream printStream = currentContext.getPrintStream();
        printStream.print(currentContext.getVarForProduction(nonterminal));
        if( nonterminal.getCardinality() == PGElement.Cardinality.ITERATION 
                || nonterminal.getCardinality() == PGElement.Cardinality.POSITIVE_ITERATION ){
            printStream.print("+=");
        }else{
            printStream.print("=");
        }
        printStream.print(getEmbeddedNonTerminalName(nonterminal.getName()));
    }
    
    @Override
    protected String getNormalizedTerminalName(PGTerminal terminal){
        if("comment".equalsIgnoreCase(terminal.getName())){
            return "ML_COMMENT";
        }else{
            String normalizedName = Util.normalizeName(terminal.getName().toUpperCase());
            return String.format("%s_TERMINAL_%s",grammar.getName().toUpperCase(),normalizedName);
        }
    }
    
    @Override
    public void visit(PGTerminal terminal) {
        PrintStream printStream = currentContext.getPrintStream();
        if(Constants.PG_EMBEDDED_DEFAULT_TERMINAL_NAME.equalsIgnoreCase(terminal.getName())){
            printStream.println("EmbeddedEmbedded");
        }else{
            printStream.println(getNormalizedTerminalName(terminal));
        }
    }
    
    @Override
    protected void printXtextNonTerminalLiteral(PrintStream printStream,PGContext context,PGLiteral literal){
        String strLiteral = literal.getString();
        String unquoted = strLiteral.substring(1, strLiteral.length()-1);
        if( literal.getUsage().wasConverted() && !context.isInstanceCreated() ){
            String normalized = Util.normalizeName(strLiteral.substring(1, strLiteral.length()-1));
            printStream.print(String.format("{f%s}",normalized));
            context.createInstance();
            currentContext.setCurrentNonterminalClass(String.format("f%s", normalized));
        }
        Map<String,String> reservedTerminals = Constants.getReservedCRSXTerminals();
        if(reservedTerminals.containsKey(unquoted)){
            printStream.println(reservedTerminals.get(unquoted));
        }else{
            printStream.print(literal.getString());
        }
        context.insertVarStub();
    }
}
