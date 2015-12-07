/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.crsx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



import net.sf.crsx.pgutil.Constants;
import net.sf.crsx.pgutil.common.Util;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGKeywordsCollector;
import net.sf.crsx.pgutil.pggrammar.PGNonTerminal;
import net.sf.crsx.pgutil.pggrammar.PGOutlineHints.PGOutlineHint;
import net.sf.crsx.pgutil.pggrammar.PGTerminal;

public class GrammarInfo{
    
    String name;
    String prefix;
    PGNonTerminal defaultNonterminal;
    List<TerminalRuleInfo> terminalRules;
    List<TerminalRuleInfo> terminalFragmentRules;
    List<PGNonTerminal> exportedNonTerminals;
    Collection<PGNonTerminal> nonterminals;
    List<KeywordInfo> keywords;
    Collection<PGOutlineHint> outlineElements;
    
    String xtextTerminalRules;
    String xtextGrammarRules;
    
    public GrammarInfo(PGGrammar grammar){
        
        grammar.addMetaRules();
        grammar.addInjectRules();
        
        name = grammar.getName();
        prefix = grammar.getPrefix();
        defaultNonterminal = grammar.getDefaultExportedNonTerminal();
        
        exportedNonTerminals = grammar.getExportedNonTerminals();
        nonterminals = grammar.getNonTerminals();
        
        terminalRules = new LinkedList<TerminalRuleInfo>();
        terminalFragmentRules = new LinkedList<TerminalRuleInfo>();
        AntlrLexerGenerator lexerGenerator = new AntlrLexerGenerator(grammar);
        
        Collection<PGTerminal> terminals = grammar.getTerminals();
        
        for(PGTerminal terminal : terminals ){
            if( terminal.getName().equalsIgnoreCase(Constants.PG_COMMENT_DEFAULT_TERMINAL_NAME)
                || terminal.getName().equalsIgnoreCase(Constants.PG_WHITESPACE_DEFAULT_TERMINAL_NAME) 
                || terminal.getName().equalsIgnoreCase(Constants.PG_EMBEDDED_DEFAULT_TERMINAL_NAME)){
                continue;
            }
            
            String ruleStr = lexerGenerator.getAntlrRuleForTerminal(terminal);
            TerminalRuleInfo rule = new TerminalRuleInfo(terminal.getName(),ruleStr);
            if(terminal.isFragment()){
                terminalFragmentRules.add(rule);
            }else{
                terminalRules.add(rule);
            }
        }
        
        EmbeddedXtextGrammarGenerator grammarGenerator = new EmbeddedXtextGrammarGenerator(grammar);
        
        //xtextGrammarRules = "//Put grammar rules here";
        xtextGrammarRules = grammarGenerator.getXtextGrammarRules();
        //xtextTerminalRules = "//Put terminal rules here";
        xtextTerminalRules = grammarGenerator.getXtextTerminalRules();
        
        outlineElements = grammar.getOutlineHints().getOutlineHints();
        
        PGKeywordsCollector keywordsCollector = new PGKeywordsCollector(grammar);
        Set<String> keywordsSet = keywordsCollector.getKeywords();
        keywords = new ArrayList<KeywordInfo>();
        Map<String,String> reservedTerminals = Constants.getReservedCRSXTerminals();
        for( String kwString : keywordsSet ){
            if( reservedTerminals.containsKey(kwString) ){
                continue;
            }
            keywords.add(new KeywordInfo(Util.transliterate(kwString),Util.escapeUnicode(kwString)));
        }
    }
    
    public String getName(){
        return name;
    }
    
    public String getPrefix(){
        return prefix;
    }
    
    public List<KeywordInfo> getKeywords(){
        return keywords;
    }
    
    public List<TerminalRuleInfo> getTerminalRules(){
        return terminalRules;
    }
    
    public List<TerminalRuleInfo> getTerminalFragmentRules(){
        return terminalFragmentRules;
    }
    
    public List<PGNonTerminal> getExportedNonTerminals(){
        return exportedNonTerminals;
    }
    
    public Collection<PGNonTerminal> getNonterminals(){
        return nonterminals;
    }
    
    public PGNonTerminal getDefaultNonTerminal(){
        return defaultNonterminal;
    }
    
    public String getXtextGrammarRules(){
        return xtextGrammarRules;
    }
    
    public String getXtextTerminalRules(){
        return xtextTerminalRules;
    }
    
    public Collection<PGOutlineHint> getOutlineElements(){
        return outlineElements;
    }
    
    public class TerminalRuleInfo{
        String name;
        String rule;
        
        public TerminalRuleInfo(String name,String rule){
            this.name = name;
            this.rule = rule;
        }
        
        public String getTerminalName(){
            return name;
        }
        
        public String getRule(){
            return rule;
        }
    }
    
    public class KeywordInfo {
        String name;
        String value;
        
        public KeywordInfo(String name,String value){
            this.name = name;
            this.value = value;
        }
        
        public String getName(){
            return name;
        }
        
        public String getValue(){
            return value;
        }
    }
}