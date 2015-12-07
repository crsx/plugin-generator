/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.crsx;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.crsx.pgutil.PGContext;
import net.sf.crsx.pgutil.common.Util;
import net.sf.crsx.pgutil.pggrammar.PGCharacters;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGLiteral;
import net.sf.crsx.pgutil.pggrammar.PGTerminal;
import net.sf.crsx.pgutil.pggrammar.PGCharacters.PGCharactersBase;
import net.sf.crsx.pgutil.xtextgen.XtextGrammarGenerator;

public class AntlrLexerGenerator extends XtextGrammarGenerator{
    
    Map<String,String> tokenRules;
    String currentTerminalRule;
    PGGrammar grammar;
    
    public AntlrLexerGenerator(PGGrammar grammar){
        tokenRules = new TreeMap<String,String>();
        this.grammar = grammar;
    }

    public String getAntlrRuleForTerminal(PGTerminal terminal){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        
        currentContext = new PGContext(printStream);
        currentContext.setMode(PGContext.Mode.PG_TERMINAL);
        terminal.getAlternative().visit(this);
        currentContext = null;
        
        String result = new String(baos.toByteArray(),java.nio.charset.StandardCharsets.UTF_8);
        return result;
    }
    
    @Override
    protected String getNormalizedTerminalName(PGTerminal terminal){
        String normalizedName = Util.normalizeName(terminal.getName().toUpperCase());
        String grammarNameUpper = grammar.getName().toUpperCase();
        return String.format("RULE_%s_TERMINAL_%s",grammarNameUpper,normalizedName);
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
        printStream.print( characters.isNegated() ? "~(":"(");
        for( PGCharactersBase chars : elements ){
            if(first){
                first = false;
            }else{
                printStream.print("|");
            }
            printStream.print(chars.getAntlrString());
        }
        printStream.print(")");
    }
    
    @Override
    public void visit(PGLiteral literal) {
        PrintStream printStream = currentContext.getPrintStream();
        String escaped = Util.escapeUnicode(literal.getString().replaceAll("'", "\\\\'"));
        printStream.print(String.format("'%s'", escaped));
    }

}
