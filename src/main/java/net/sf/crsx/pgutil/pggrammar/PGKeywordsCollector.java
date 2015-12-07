/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.Set;
import java.util.TreeSet;

public class PGKeywordsCollector implements PGElementVisitor{

    protected PGGrammar grammar;
    Set<String> keywords;
    
    
    public PGKeywordsCollector(PGGrammar grammar){
        this.grammar = grammar;
        keywords = new TreeSet<String>();
    }
    
    public Set<String> collectKeywordsSet(){
        keywords.clear();
        this.visit(grammar);
        return keywords;
    }
    
    public Set<String> getKeywords(){
        if( keywords.isEmpty() ){
            return collectKeywordsSet();
        }else{
            return keywords;
        }
    }
    
    @Override
    public void visit(PGAlternative alternative) {
        for(PGChoice choice : alternative.getAlternatives() ){
            choice.visit(this);
        }
    }

    @Override
    public void visit(PGCharacters characters) {
        // Do nothing
    }

    @Override
    public void visit(PGChoice choice) {
        for( PGElement element : choice.getElements() ){
            element.visit(this);
        }
    }

    @Override
    public void visit(PGGeneration generation) {
        // Do nothing
    }

    @Override
    public void visit(PGInline inline) {
        // Do nothing
    }

    @Override
    public void visit(PGLiteral literal) {
        String literalStr = literal.getString();
        String unquoted = literalStr.substring(1, literalStr.length()-1);
        keywords.add(unquoted);
    }

    @Override
    public void visit(PGNonTerminal nonterminal) {
        nonterminal.getAlternatives().visit(this);
    }

    @Override
    public void visit(PGTerminal terminal) {
        // Do nothing
    }

    @Override
    public void visit(PGGrammar grammar) {
        for( PGNonTerminal nonterminal : grammar.getNonTerminals() ){
            nonterminal.visit(this);
        }
    }

    @Override
    public void visit(PGArtificial artificial) {
        // Do nothing
    }

}
