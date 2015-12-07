/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import net.sf.crsx.pgutil.PGContext;

public class PGInline extends PGElement{

    String inline;
    
    public PGInline(String inline){
        this.inline = inline;
    }
    
    public String getInlineStr(){
        return inline;
    }
    
    @Override
    public boolean isPrintable(PGContext context){
        return getUsage().wasConverted() && !context.isInstanceCreated();
    }
    
    @Override
    public int getPrintableCount(PGContext context){
        return isPrintable(context) ? 1 : 0;
    }
    
    @Override
    public void visit(PGElementVisitor visitor){
        visitor.visit(this);
    }
}
