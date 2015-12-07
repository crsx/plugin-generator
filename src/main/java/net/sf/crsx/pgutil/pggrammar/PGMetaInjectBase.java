/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

public class PGMetaInjectBase {
    PGChoice choice;
    String begin,end;
    
    
    public PGMetaInjectBase(PGChoice choice, String begin, String end){
        this.choice = choice;
        this.begin = begin;
        this.end = end;
    }
    
    public PGChoice getChoice(){
        return choice;
    }
    
    public String getBegin(){
        return begin;
    }
    
    public String getEnd(){
        return end;
    }
    
}
