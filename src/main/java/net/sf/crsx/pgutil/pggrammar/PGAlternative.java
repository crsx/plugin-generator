/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.ArrayList;
import java.util.List;

import net.sf.crsx.pgutil.PGContext;

public class PGAlternative extends PGChoice{
    List<PGChoice> alternatives;
    
    public PGAlternative(){
        alternatives = new ArrayList<PGChoice>();
    }
    
    public void addAlternative(PGChoice choice){
        alternatives.add(choice);
    }
    
    public List<PGChoice> getAlternatives(){
        return alternatives;
    }
    
    @Override
    public boolean isPrintable(PGContext context){
        for( PGChoice choice : alternatives ){
            if(choice.isPrintable(context))
                return true;
        }
        return false;
    }
    
    @Override
    public int getPrintableCount(PGContext context){
        int count = 0;
        for( PGChoice choice : alternatives ){
            if(choice.isPrintable(context))
                count++;
        }
        return count;
    }
    
    @Override
    public void visit(PGElementVisitor visitor){
        visitor.visit(this);
    }
}
