/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

public class PGPluginGeneratorProperties {
    
    public enum Mode {
        HIDDEN,
        FORMAT_STRING
    }
    
    public enum RefMode {
        NONE,
        ID,
        REFERENCE
    }
    
    private Mode mode;
    private String formatString;
    private boolean leaf;
    
    private RefMode refMode;
    
    private int idx;
    private String idxRefLabel;
    
    
    public PGPluginGeneratorProperties(){
        mode = Mode.HIDDEN;
        leaf = false;
        refMode = RefMode.NONE;
    }
    
    public PGPluginGeneratorProperties(String formatString){
        mode = Mode.FORMAT_STRING;
        this.formatString = formatString;
    }
    
    public Mode getMode(){
        return mode;
    }
    
    public RefMode getRefMode(){
        return refMode;
    }
    
    public void setRefMode(RefMode refMode){
        this.refMode = refMode;
    }
    
    public void setIdx(int value){
        this.idx = value;
    }
    
    public int getIdx(){
        return idx;
    }
    
    public String getIdxRefLabel(){
        return idxRefLabel;
    }
    
    public void setIdxRefLabel(String value){
        this.idxRefLabel = value;
    }
    
    public String getFormatString(){
        return formatString;
    }
    
    public void setFormatString(String formatString){
        this.formatString = formatString;
    }
    
    public void setLeaf(boolean value){
        this.leaf = value;
    }
    
    public boolean isLeaf(){
        return leaf;
    }
}
