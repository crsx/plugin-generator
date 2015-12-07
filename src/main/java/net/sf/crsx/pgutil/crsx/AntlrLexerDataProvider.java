/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.crsx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;

import net.sf.crsx.pgutil.Constants;
import net.sf.crsx.pgutil.common.TemplateFileProvider;
import net.sf.crsx.pgutil.crsx.GrammarInfo.KeywordInfo;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;

public class AntlrLexerDataProvider implements TemplateFileProvider.TemplateDataProvider{

    List<PGGrammar> embeddedGrammars;
    
    Map<String,Object> data;
    List<UnifiedKeyword> unifiedKeywords;
    
    public AntlrLexerDataProvider(List<PGGrammar> embeddedGrammars){
        this.embeddedGrammars = embeddedGrammars;
        
        data = new TreeMap<String,Object>();
        
        List<GrammarInfo> grammars = new LinkedList<GrammarInfo>();
        for(PGGrammar grammar : embeddedGrammars ){
            grammars.add( new GrammarInfo(grammar) );
        }
        
        List<UnifiedKeyword> keywords = initUnifiedKeywordsList(grammars);
        
        data.put("embeddedGrammars",grammars);
        data.put("keywords", keywords );
        data.put("package", Constants.GENERATED_CRSX_PLUGIN_PACKAGE );
    }
    
    protected List<UnifiedKeyword> initUnifiedKeywordsList(List<GrammarInfo> grammars){
        
        Map<String,UnifiedKeyword> unifiedKeywordMap= new TreeMap<String,UnifiedKeyword>();
        for(GrammarInfo grammar : grammars){
            for(KeywordInfo keywordInfo : grammar.getKeywords()){
                UnifiedKeyword keyword = unifiedKeywordMap.get(keywordInfo.getName());
                if(keyword == null){
                    keyword = new UnifiedKeyword(keywordInfo.getName(),keywordInfo.getValue());
                    unifiedKeywordMap.put(keyword.getName(), keyword);
                }
                keyword.addFeasibleGrammarName(grammar.getName().toUpperCase());
            }
        }
        
        List<UnifiedKeyword> keywords = new ArrayList<UnifiedKeyword>();
        keywords.addAll(unifiedKeywordMap.values());
        Collections.sort(keywords);
        return keywords;
    }
    
    @Override
    public Object getData() {
        return data;
    }
    
    public static final class UnifiedKeyword implements Comparable<UnifiedKeyword>{
        String name;
        String value;
        List<String> feasibleGrammarNames;
        
        public UnifiedKeyword( String keywordId, String keywordString ){
            this.name = keywordId;
            this.value = keywordString;
            feasibleGrammarNames = new LinkedList<String>();
        }
        
        public String getName(){
            return name;
        }
        
        public String getValue(){
            return value;
        }
        
        public void addFeasibleGrammarName(String grammarName){
            feasibleGrammarNames.add(grammarName);
        }
        
        public List<String> getFeasibleGrammarNames(){
            return feasibleGrammarNames;
        }
        
        @Override
        public boolean equals(Object obj){
            if( ! (obj instanceof UnifiedKeyword) ){
                return false;
            }
            if( obj == this ){
                return true;
            }
            
            UnifiedKeyword other = (UnifiedKeyword) obj;
            
            return name.equals(other.name);
        }
        
        @Override
        public int hashCode(){
            return name.hashCode();
        }

        @Override
        public int compareTo(UnifiedKeyword other) {
            String s1 = StringEscapeUtils.unescapeJava(value);
            String s2 = StringEscapeUtils.unescapeJava(other.value);
            
            if( s1.length() == s2.length() ){
                return s1.compareTo(s2);
            }else{
                if( s1.length() > s2.length() ){
                    return -1;
                }else{
                    return 1;
                }
            }
        }
        
    }
}
