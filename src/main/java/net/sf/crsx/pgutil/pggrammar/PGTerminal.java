/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import net.sf.crsx.pgutil.PGContext;

public class PGTerminal extends PGElement {
    public enum TerminalType {
        TERMINAL_TYPE_EOF,
        TERMINAL_TYPE_REGEX
    }
    
    String name;
    PGAlternative alternative;
    boolean isFragment;
    
    public PGTerminal(String name){
        this.name = name;
        isFragment = false;
    }
    
    public void setAlternative(PGAlternative alternative){
        this.alternative = alternative;
    }
    
    public void setFragmentFlag(boolean isFragment){
        this.isFragment = isFragment;
    }
    
    public boolean isFragment(){
        return isFragment;
    }
    
    public PGAlternative getAlternative(){
        return alternative;
    }
    
    public String getName(){
        return name;
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
