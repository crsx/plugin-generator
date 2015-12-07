/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.List;

import net.sf.crsx.pgutil.PGContext;

public class PGNonTerminal extends PGElement{
    public static final byte EXPORTED = 1;
    public static final byte EXPORTED_ITERATION = 2;
    public static final byte EXPORTED_POSITIVE_ITERATION = 4;
    public static final byte EXPORTED_OPTIONAL = 8;
    
    String name;
    byte exportedFlags;
    PGAlternative alternatives;
    boolean stringRule;
    
    public PGNonTerminal( String name ){
        this.name = name;
        exportedFlags = 0;
        alternatives = new PGAlternative();
        stringRule = false;
    }
    
    public void setExported(boolean exported){
        if(!exported){
            exportedFlags = 0;
        }
        exportedFlags |= EXPORTED;
    }
    
    public void setExportedFlag(byte flag,boolean state){
        if(state){
            exportedFlags |= flag;
        }else{
            exportedFlags &= ~flag;
        }
    }
    
    public boolean getExportedFlag(byte flag){
        return (exportedFlags & flag) != 0;
    }
    
    public byte getExportedFlags(){
        return exportedFlags;
    }
    
    public void setExportedFlags(byte flags){
        exportedFlags = flags;
    }
    
    public boolean isExported(){
        return exportedFlags != 0;
    }
    
    public boolean isExportedIteration(){
        return getExportedFlag(EXPORTED_ITERATION);
    }
    
    public boolean isExportedPositiveIteration(){
        return getExportedFlag(EXPORTED_POSITIVE_ITERATION);
    }
    
    public boolean isExportedOptional(){
        return getExportedFlag(EXPORTED_OPTIONAL);
    }
    
    public boolean isExportedSingle(){
        return exportedFlags == EXPORTED;
    }
    
    public String getName(){
        return name;
    }
    
    public boolean isStringRule(){
        return stringRule;
    }
    
    public void setStringRule(boolean value){
        this.stringRule = value;
    }
    
    public void addAlternative(PGChoice choice){
        alternatives.addAlternative(choice);
    }
    
    public PGAlternative getAlternatives(){
        return alternatives;
    }
    
    public List<PGChoice> getChoices(){
        return alternatives.getAlternatives();
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
