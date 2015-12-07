/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.crsx.pgutil.pggrammar.PGElement;
import net.sf.crsx.pgutil.pggrammar.PGNonTerminal;
import net.sf.crsx.pgutil.pggrammar.PGOutlineHints;
import net.sf.crsx.pgutil.pggrammar.PGTerminal;

public class PGContext {
    public static enum Mode{
        PG_NONTERMINAL,
        PG_TERMINAL
    }
    
    private Mode mode = Mode.PG_NONTERMINAL;
    private boolean instanceCreated;
    private Set<String> instanceVars;
    private List<String> instanceVarList;
    private PGNonTerminal currentNonTerminal;
    private String currentClassname;
    private PrintStream printStream;
    private PGOutlineHints outlineHints;
    private boolean stringRule;
    
    private boolean referencedObject;
    private int referencedElementIdx;
    
    private Map<String,String> referenceLabelToObject;
    
    private boolean nextVarNameReferenced;
    private String nextReferenceLabel;
    
    public PGContext(){
        instanceCreated = false;
        instanceVars = new TreeSet<String>();
        instanceVarList = new ArrayList<String>();
        currentNonTerminal = null;
        outlineHints = new PGOutlineHints();
        stringRule = false;
        
        referencedObject = false;
        nextVarNameReferenced = false;
        referencedElementIdx = -1;
        referenceLabelToObject = new TreeMap<String,String>();
        nextReferenceLabel = null;
    }
    
    public PGContext(PrintStream ps){
        printStream = ps;
        instanceCreated = false;
        instanceVars = new TreeSet<String>();
        instanceVarList = new ArrayList<String>();
        currentNonTerminal = null;
        outlineHints = new PGOutlineHints();
        stringRule = false;
        
        referencedObject = false;
        nextVarNameReferenced = false;
        referencedElementIdx = -1;
        referenceLabelToObject = new TreeMap<String,String>();
        nextReferenceLabel = null;
    }
    
    public PGOutlineHints getOutlineHints(){
        return outlineHints;
    }
    
    public PGNonTerminal getCurrentNonTerminal(){
        return currentNonTerminal;
    }
    
    public void setCurrentNonTerminal(PGNonTerminal nonterminal){
        currentNonTerminal = nonterminal;
    }
    
    public void createInstance(){
        instanceCreated = true;
        instanceVars.clear();
        instanceVarList = new ArrayList<String>();
    }
    
    public void setMode(Mode mode){
        this.mode = mode;
    }
    
    public Mode getMode(){
        return mode;
    }
    
    public boolean isStringRule(){
        return stringRule;
    }
    
    public void setStringRule(boolean value){
        this.stringRule = value;
    }
    
    public boolean isReferencedObject(){
        return this.referencedObject;
    }
    
    public void setNextReferenceLabel(String label){
        this.nextReferenceLabel = label;
    }
    
    public String getNextReferenceLabel(){
        return nextReferenceLabel;
    }
    
    public void setReferencedObject(boolean value){
        this.referencedObject = value;
    }
    
    public int getReferencedElementIdx(){
        return referencedElementIdx;
    }
    
    public void setReferencedElementIdx(int value){
        this.referencedElementIdx = value;
    }
    
    public void setReferenceLabel(String label,String referencedObjName){
        referenceLabelToObject.put(label, referencedObjName);
    }
    
    public String getReferencedObjectName(String label){
        return referenceLabelToObject.get(label);
    }
    
    public void setCurrentNonterminalClass(String currentClassname){
        this.currentClassname = currentClassname;
    }
    
    public String getCurrentNonterminalClass(){
        return currentClassname;
    }
    
    public void setNextNameReferenced(boolean value){
        this.nextVarNameReferenced = value;
    }
    
    public boolean isNextNameReferenced(){
        return nextVarNameReferenced;
    }
    
    public void destroyInstance(){
        instanceCreated = false;
        instanceVars.clear();
    }
    
    public boolean isInstanceCreated(){
        return instanceCreated;
    }
    
    public PrintStream getPrintStream(){
        return printStream;
    }
    
    public List<String> getInstanceVarList(){
        return instanceVarList;
    }
    
    public void insertVarStub(){
        if(instanceVarList != null){
            instanceVarList.add(null);
        }
    }
    
    public String getVarForProduction(PGNonTerminal nonterminal){
        if(nextVarNameReferenced){
            nextVarNameReferenced = false;
            String result = "name";
            instanceVars.add(result);
            instanceVarList.add(result);
            return result;
        }
        String nonterminalName = nonterminal.getName();
        String baseName = "m"+Character.toUpperCase(nonterminalName.charAt(0))+
                nonterminalName.substring(1, nonterminalName.length());
        if( nonterminal.getCardinality() == PGElement.Cardinality.ITERATION 
                || nonterminal.getCardinality() == PGElement.Cardinality.POSITIVE_ITERATION ){
            baseName += "List";
        }
        int trial = 0;
        String name = baseName;
        while(instanceVars.contains(name)){
            ++trial;
            name = baseName + Integer.toString(trial);
        }
        instanceVars.add(name);
        instanceVarList.add(name);
        return name;
    }
    
    public String getVarForTerminal(PGTerminal terminal){
        if(nextVarNameReferenced){
            nextVarNameReferenced = false;
            String result = "name";
            instanceVars.add(result);
            instanceVarList.add(result);
            return result;
        }
        String terminalName = terminal.getName();
        String baseName = "m"+Character.toUpperCase(terminalName.charAt(0))+
                terminalName.substring(1, terminalName.length());
        if( terminal.getCardinality() == PGElement.Cardinality.ITERATION 
                || terminal.getCardinality() == PGElement.Cardinality.POSITIVE_ITERATION ){
            baseName += "List";
        }
        int trial = 0;
        String name = baseName;
        while(instanceVars.contains(name)){
            ++trial;
            name = baseName + Integer.toString(trial);
        }
        instanceVars.add(name);
        instanceVarList.add(name);
        return name;
    }
}
