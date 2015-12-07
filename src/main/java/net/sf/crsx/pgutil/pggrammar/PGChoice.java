/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.ArrayList;
import java.util.List;

import net.sf.crsx.pgutil.PGContext;

public class PGChoice extends PGElement{

    List<PGElement> elements;
    PGPluginGeneratorProperties pluginGeneratorProperties;
    
    public PGChoice(){
        elements = new ArrayList<PGElement>();
    }
    
    public void addElement(PGElement element){
        elements.add(element);
    }
    
    public List<PGElement> getElements(){
        return elements;
    }
    
    @Override
    public boolean isPrintable(PGContext context){
        for(PGElement element : elements ){
            if( element.isPrintable(context) )
                return true;
        }
        return false;
    }
    
    @Override
    public int getPrintableCount(PGContext context){
        int count = 0;
        for(PGElement element : elements ){
            if( element.isPrintable(context) )
                ++count;
        }
        return count;
    }
    
    @Override
    public void visit(PGElementVisitor visitor){
        visitor.visit(this);
    }
    
    public void setPluginGeneratorProperties(PGPluginGeneratorProperties outlineProperties){
        this.pluginGeneratorProperties = outlineProperties;
    }
    
    public PGPluginGeneratorProperties getPluginGeneratorProperties(){
        return pluginGeneratorProperties;
    }
}
