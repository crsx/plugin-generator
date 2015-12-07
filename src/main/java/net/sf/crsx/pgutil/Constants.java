/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil;

import java.util.Map;
import java.util.TreeMap;

public class Constants {
    
    public static final String GENERATED_PLUGIN_DEFAULT_VERSION = "0.1.0";
    public static final String GENERATED_PLUGIN_DEFAULT_VENDOR  = "CRSX";
    
    public static final String GENERATED_CRSX_PLUGIN_PACKAGE = "net.sf.crsx.xtext";
    
    public static final String PG_COMMENT_DEFAULT_TERMINAL_NAME = "comment";
    public static final String PG_WHITESPACE_DEFAULT_TERMINAL_NAME = "skip";
    public static final String PG_EMBEDDED_DEFAULT_TERMINAL_NAME = "embedded";
    
    public static final String DEFAULT_XTEXT_VERSION = "2.9.0";
    public static final String DEFAULT_XTEXT_UPDATE_SITE = 
            String.format("http://download.eclipse.org/modeling/tmf/xtext/updates/releases/%s",DEFAULT_XTEXT_VERSION);
    
    static final Map<String,String> reservedTerminals;
    
    static{
        reservedTerminals = new TreeMap<String,String>();
        reservedTerminals.put("(", "LPAR");
        reservedTerminals.put(")", "RPAR");
        reservedTerminals.put("}", "LCURLY");
        reservedTerminals.put("{", "RCURLY");
        reservedTerminals.put("]", "LSQUARE");
        reservedTerminals.put("[", "RSQUARE");
        reservedTerminals.put(";", "SEMICOLON");
        reservedTerminals.put(":", "COLON");
        reservedTerminals.put("::", "COLONCOLON");
        reservedTerminals.put("::=", "COLONCOLONEQ");
        reservedTerminals.put(".", "DOT");
        reservedTerminals.put(",", "COMMA");
        reservedTerminals.put("$", "EVALUATOR");
        reservedTerminals.put("{", "RCURLY");
        reservedTerminals.put("¬", "NOT");
        reservedTerminals.put("→", "ARROW");
        reservedTerminals.put("∀", "POLY");
        reservedTerminals.put("?", "QUESTIONMARK");
        reservedTerminals.put("*", "ASTERISK");
        reservedTerminals.put("+", "PLUSSIGN");
        reservedTerminals.put("-", "DASH");
    }
    
    private Constants(){
        
    }
    
    public static Map<String,String> getReservedCRSXTerminals(){
        return reservedTerminals;
    }
}
