/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.ArrayList;
import java.util.List;

import net.sf.crsx.pgutil.PGContext;
import net.sf.crsx.pgutil.common.Util;

public class PGCharacters extends PGElement{
    
    List<PGCharactersBase> elements;
    boolean negated;
    
    public PGCharacters(){
        elements = new ArrayList<PGCharactersBase>();
        negated = false;
    }
    
    public void setNegated(boolean negated){
        this.negated = negated;
    }
    
    public boolean isNegated(){
        return negated;
    }
    
    public void addElement(PGCharactersBase element){
        elements.add(element);
    }
    
    @Override
    public boolean isPrintable(PGContext context){
        return true;
    }
    
    @Override
    public int getPrintableCount(PGContext context){
        return 1;
    }
    
    public List<PGCharactersBase> getElements(){
        return elements;
    }
    
    public abstract static class PGCharactersBase {
        
        public abstract String getXtextString();
        
        public String getAntlrString(){
            return Util.escapeUnicode(getXtextString());
        }
    }
    
    public static class PGCharactersElement extends PGCharactersBase{
        String literal;
        
        public PGCharactersElement(String literal){
            this.literal = literal;
        }
        
        @Override
        public String getXtextString(){
            return String.format("'%s'", literal.replace("'", "\\'"));
        }
    }
    
    public static class PGCharactersRange extends PGCharactersBase {
        char from,to;
        
        public PGCharactersRange(char from, char to){
            this.from = from;
            this.to = to;
        }
        
        @Override
        public String getXtextString(){
            return String.format("'%c'..'%c'", from,to);
        }
    }
    
    @Override
    public void visit(PGElementVisitor visitor){
        visitor.visit(this);
    }
}
