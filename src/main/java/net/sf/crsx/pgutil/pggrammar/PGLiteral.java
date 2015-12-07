/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import net.sf.crsx.pgutil.PGContext;

public class PGLiteral extends PGElement{
    String strLiteral;
    
    public PGLiteral(String str){
        strLiteral = str;
    }
    
    public String getString(){
        return strLiteral;
    }
    
    @Override
    public boolean isPrintable(PGContext context){
        return true;
    }
    
    @Override
    public int getPrintableCount(PGContext context){
        return 1;
    }
    
    @Override
    public void visit(PGElementVisitor visitor){
        visitor.visit(this);
    }
}
