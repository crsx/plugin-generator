/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;


public class PGGeneration extends PGElement{

    String generation;
    
    public PGGeneration(String generation){
        this.generation = generation;
    }
    
    @Override
    public void visit(PGElementVisitor visitor){
        visitor.visit(this);
    }
}
