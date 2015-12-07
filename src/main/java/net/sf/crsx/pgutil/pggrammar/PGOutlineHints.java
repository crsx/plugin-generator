/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGOutlineHints {
    Map<String,PGOutlineHint> outlineHintMap;
    
    public PGOutlineHints(){
        outlineHintMap = new TreeMap<String,PGOutlineHint>();
    }
    
    public void addOutlineHint(String classname,PGOutlineHint hint){
        outlineHintMap.put(classname, hint);
    }
    
    public void addOutlineHint(String classname, PGPluginGeneratorProperties properties, List<String> members){
        String formatString = null;
        boolean isLeaf = false;
        if(properties != null ){
            formatString = properties.getFormatString();
            isLeaf = properties.isLeaf();
        }
        PGOutlineHint outlineHint = new PGOutlineHint(classname,members,formatString);
        outlineHint.setLeaf(isLeaf);
        outlineHintMap.put(classname, outlineHint);
    }
    
    public Map<String,PGOutlineHint> getOutlineHintMap(){
        return outlineHintMap;
    }
    
    public Collection<PGOutlineHint> getOutlineHints(){
        return outlineHintMap.values();
    }
    
    public static class PGOutlineHint {
        
        static final Pattern pattern = Pattern.compile("\\{(\\d+)\\}");
        
        enum Type{
            HIDDEN,
            LABELED
        }
        
        String classname;
        List<String> members;
        String formatStr;
        
        String formatStrJava;
        List<String> formatStrArgsJava;
        
        boolean isLeaf;
        
        public PGOutlineHint(String classname, List<String> members, String formatStr){
            this.classname = classname;
            this.members = members;
            this.formatStr = formatStr;
            formatStrJava = null;
            formatStrArgsJava = null;
            isLeaf = false;
        }
        
        public List<String> getMemberList(){
            return members;
        }
        
        public String getFormatStr(){
            return formatStr;
        }
        
        public String getClassname(){
            return classname;
        }
        
        public boolean isHidden(){
            return formatStr == null;
        }
        
        public void setLeaf(boolean value){
            isLeaf = value;
        }
        
        public boolean isLeaf(){
            return isLeaf;
        }
        
        protected void initJavaFmtAndArgs(){
            StringBuffer strBuf = new StringBuffer();
            formatStrArgsJava = new ArrayList<String>();
            Matcher matcher = pattern.matcher(formatStr);
            while(matcher.find()){
                matcher.appendReplacement(strBuf, "%s");
                int argIdx = Integer.parseInt(matcher.group(1));
                if( argIdx >= members.size() ){
                    System.out.println("wtf");
                    //TODO thow exception
                }
                String arg = members.get(argIdx);
                if(arg == null){
                    //TODO throw exception
                }
                formatStrArgsJava.add(arg);
            }
            matcher.appendTail(strBuf);
            formatStrJava = strBuf.toString();
        }
        
        public String getJavaFmtString(){
            if(formatStrJava == null){
                initJavaFmtAndArgs();
            }
            return formatStrJava;
        }
        
        public List<String> getFmtArgumentsList(){
            if(formatStrArgsJava == null){
                initJavaFmtAndArgs();
            }
            return formatStrArgsJava;
        }
    }
}
