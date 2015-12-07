/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import net.sf.crsx.pgutil.PGContext;

public abstract class PGElement implements PGVisitable{
    public enum Cardinality{
        SINGLE,
        ITERATION,
        POSITIVE_ITERATION,
        OPTIONAL
    }
    
    private Cardinality cardinality = Cardinality.SINGLE;
    private Usage usage = null;
    boolean used = false;
    
    public void setCardinality(Cardinality cardinality){
        this.cardinality = cardinality;
    }
    
    public Cardinality getCardinality(){
        return cardinality;
    }
    
    public void setUsage(Usage usage){
        this.usage = usage;
    }
    
    public Usage getUsage(){
        return usage;
    }
    
    public boolean isUsed(){
        return used;
    }
    
    public void setUsed(boolean used){
        this.used = used;
    }
    
    public void normalize(){
        
    }
    
    public boolean isPrintable(PGContext context){
        return false;
    }
    
    public int getPrintableCount(PGContext context){
        return 0;
    }

}
