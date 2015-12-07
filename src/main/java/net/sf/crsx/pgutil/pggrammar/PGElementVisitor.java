/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

public interface PGElementVisitor {

    public void visit(PGAlternative alternative);
    
    public void visit(PGCharacters characters);
    
    public void visit(PGChoice choice);
    
    public void visit(PGGeneration generation);
    
    public void visit(PGInline inline);
    
    public void visit(PGLiteral literal);
    
    public void visit(PGNonTerminal nonterminal);
    
    public void visit(PGTerminal terminal);
    
    public void visit(PGGrammar grammar);
    
    public void visit(PGArtificial artificial);
}
