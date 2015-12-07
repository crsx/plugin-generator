/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import net.sf.crsx.pgutil.PGContext;

public class PGArtificial extends PGElement {

    String content;
    
    public PGArtificial(String content){
        this.content = content;
    }
    
    public void setContent(String content){
        this.content = content;
    }
    
    public String getContent(){
        return content;
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
    public void visit(PGElementVisitor visitor) {
        visitor.visit(this);
    }

}
