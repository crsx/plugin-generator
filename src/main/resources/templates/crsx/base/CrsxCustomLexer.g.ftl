
/*
 * Custom Lexer implementation to deal with overlapping sets of terminals
 */
lexer grammar CrsxCustomLexer;


@header {
package net.sf.crsx.xtext.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer
import ${package}.lexer.Lexer;

import java.util.Stack;
}


@members {
    
    enum State {
        DEFAULT,
        EMBEDDED_TEXT,
        EMBEDDED_TEXT_BEFORE_BRACKET,
<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
        EMBEDDED_${embeddedGrammar.name?upper_case},
        EMBEDDED_${embeddedGrammar.name?upper_case}_BEFORE_BRACKET,
    </#list>
</#if>
        EMBEDDED_OTHER,
        EMBEDDED_OTHER_BEFORE_BRACKET
    };
    
    boolean expectEmbeddedMetaArg = false;
    
    Stack<State> stack = new Stack<State>();
    
    State getCurrentState(){
        if(stack.empty()){
            return State.DEFAULT;
        }else{
            return stack.peek();
        }
    }
    
    public void switchCurrentState(State state){
        if(!stack.empty()){
            stack.pop();
        }
        stack.push(state);
    }
    
    public void processEmbeddingOpen(){
        switch( getCurrentState() ){
            case EMBEDDED_OTHER:
            case EMBEDDED_TEXT:
<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
            case EMBEDDED_${embeddedGrammar.name?upper_case}:
    </#list>
</#if>
                stack.push(State.DEFAULT);
                break;
            case EMBEDDED_TEXT_BEFORE_BRACKET:
                switchCurrentState(State.EMBEDDED_TEXT);
                break;
<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
            case EMBEDDED_${embeddedGrammar.name?upper_case}_BEFORE_BRACKET:
                switchCurrentState(State.EMBEDDED_${embeddedGrammar.name?upper_case});
                break;
    </#list>
</#if>
            case EMBEDDED_OTHER_BEFORE_BRACKET:
                switchCurrentState(State.EMBEDDED_OTHER);
                break;
            case DEFAULT:
            default:
                //Copy current state on stack
                stack.push(getCurrentState());
        }
    }
}

<#list keywords as keyword>
${keyword.name} : {<#list keyword.feasibleGrammarNames as grammarName> getCurrentState() == State.EMBEDDED_${grammarName?upper_case}<#sep> ||</#sep></#list> }?=> '${keyword.value}' {expectEmbeddedMetaArg = false;};
</#list>


RULE_LID :  { getCurrentState() == State.DEFAULT }?=> RULE_LINEAR? RULE_LOWER (RULE_ALPHANUMERIC|RULE_OTHER|RULE_DASH|RULE_UNDERSCORE)* RULE_LINEAR? RULE_FUNCTIONAL?;

RULE_UID :{ getCurrentState() == State.DEFAULT }?=> ((RULE_UPPER|RULE_DASH|RULE_UNDERSCORE) (RULE_ALPHANUMERIC|RULE_DASH|RULE_UNDERSCORE)*|('@'|'^'|'*'|'+'|'`'|'|'|'#'|'/'|'?'|'='|'~')+);

fragment RULE_LINEAR : '\u00B9';

fragment RULE_FUNCTIONAL : '\u1D47';

RULE_INTERNAL : { getCurrentState() == State.DEFAULT }?=> '$' (RULE_UID|RULE_LID);

RULE_EVALUATOR : { getCurrentState() == State.DEFAULT }?=> '$';

RULE_DASH : '-';

RULE_METAVARIABLE : { getCurrentState() == State.DEFAULT }?=> '#' (RULE_LOWER|RULE_UPPER|RULE_DIGIT|RULE_OTHER|RULE_DASH|RULE_UNDERSCORE|RULE_ASTERISK|RULE_PLUSSIGN|RULE_QUESTIONMARK)*;

RULE_NUMERIC : { getCurrentState() == State.DEFAULT }?=> RULE_DIGIT+ ('.' RULE_DIGIT+)? (('E'|'e') RULE_DIGIT+)?;

RULE_NOT : { getCurrentState() == State.DEFAULT }?=> '\u00AC';

RULE_ATOM : { getCurrentState() == State.DEFAULT }?=> '\'' ( options {greedy=false;} : . )*'\'';

RULE_STRING : { getCurrentState() == State.DEFAULT }?=> '"' ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\')|~(('\\'|'"')))* '"';

RULE_ARROW : { getCurrentState() == State.DEFAULT }?=> '\u2192';

RULE_POLY : { getCurrentState() == State.DEFAULT }?=> '\u2200';

RULE_DOT : '.';

RULE_ASTERISK : '*';

RULE_PLUSSIGN : '+';

RULE_QUESTIONMARK : '?';

RULE_EMBEDDED_TEXT : { getCurrentState() == State.DEFAULT }?=> '%n'{ stack.push(State.EMBEDDED_TEXT_BEFORE_BRACKET); };

<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
// ${embeddedGrammar.name?upper_case} embedding terminals
RULE_EMBEDDED_${embeddedGrammar.name?upper_case} : { getCurrentState() == State.DEFAULT }?=> '%${embeddedGrammar.prefix}' {stack.push(State.EMBEDDED_${embeddedGrammar.name?upper_case}_BEFORE_BRACKET);};

<#list embeddedGrammar.exportedNonTerminals as exportedNonTerminal>
RULE_EMBEDDED_${embeddedGrammar.name?upper_case}_${exportedNonTerminal.name?upper_case} : { getCurrentState() == State.DEFAULT }?=> '%${embeddedGrammar.prefix}${exportedNonTerminal.name}' {stack.push(State.EMBEDDED_${embeddedGrammar.name?upper_case}_BEFORE_BRACKET);};

</#list>
    </#list>
</#if>

RULE_EMBEDDED_OTHER : { getCurrentState() == State.DEFAULT }?=> '%' ~('n'<#if embeddedGrammars??><#list embeddedGrammars as embeddedGrammar>|'${embeddedGrammar.prefix}'</#list></#if>) ( RULE_LOWER | RULE_UPPER | RULE_DIGIT | '_')* ('*'|'?'|'+')? {stack.push(State.EMBEDDED_OTHER_BEFORE_BRACKET);};

RULE_COLONCOLONEQ : '::=';

RULE_COLONCOLON : '::';

RULE_COLON : ':';

RULE_SEMICOLON : ';';

RULE_COMMA : ',';

RULE_UNDERSCORE : '_';

RULE_LCURLY : '{';

RULE_RCURLY : '}';

RULE_LSQUARE : '[' {
 if( expectEmbeddedMetaArg ){
    processEmbeddingOpen();
 }
};

RULE_RSQUARE : ']'{
 if( expectEmbeddedMetaArg ){
    if(!stack.empty()){
        stack.pop();
    }
    expectEmbeddedMetaArg = false;
 }
};
RULE_LPAR : '(';

RULE_RPAR : ')';

RULE_LEMBEDDED3 : '\u27E6' {
 if( getCurrentState() == State.EMBEDDED_TEXT ){
    stack.push(State.EMBEDDED_TEXT);
 }else{
    processEmbeddingOpen();
 }
 
};

RULE_LEMBEDDED4 : '\u27E8' { processEmbeddingOpen(); };

RULE_LEMBEDDED5 : '\u27EA' { processEmbeddingOpen(); };

RULE_LEMBEDDED6 : '\u2983' {
 if( getCurrentState() == State.EMBEDDED_TEXT ){
    stack.push(State.EMBEDDED_TEXT);
 }else{
    processEmbeddingOpen();
 }
};

RULE_LEMBEDDED7 : '\u2308' { processEmbeddingOpen(); };

RULE_LEMBEDDED8 : '\u230A' { processEmbeddingOpen(); };

RULE_LEMBEDDED9 : '\u2768' { processEmbeddingOpen(); };

RULE_LEMBEDDED10 : '\u00AB' { processEmbeddingOpen(); };

RULE_LEMBEDDED11 : '\u2039' { processEmbeddingOpen(); };

RULE_LEMBEDDED12 : '\u29FC' { processEmbeddingOpen(); };

RULE_LEMBEDDED13 : '\u2018' { processEmbeddingOpen(); };

RULE_REMBEDDED3 : '\u27E7' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED4 : '\u27E9' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED5 : '\u27EB' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED6 : '\u2984' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED7 : '\u2309' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED8 : '\u230B' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED9 : '\u2769' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED10 : '\u00BB' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED11 : '\u203A' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED12 : '\u29FD' { if (!stack.empty()){ stack.pop();}; } ;

RULE_REMBEDDED13 : '\u2019' { if (!stack.empty()){ stack.pop();}; } ;


fragment RULE_START_XML : '<!--';

fragment RULE_END_XML : '-->';

RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

RULE_XML_COMMENT : RULE_START_XML ( options {greedy=false;} : . )*RULE_END_XML;

RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_WS : (' '|'\t'|'\r'|'\n')+;

fragment RULE_ALPHANUMERIC : (RULE_UPPER|RULE_LOWER|RULE_DIGIT);

fragment RULE_DIGIT : '0'..'9';

fragment RULE_UPPER : 'A'..'Z';

fragment RULE_LOWER : 'a'..'z';

fragment RULE_OTHER : ('@'|'^'|'*'|'+'|'-'|'`'|'|'|'#'|'/'|'!'|'?'|'%'|'='|'~'|'\u2190'..'\u21FF');

fragment RULE_META_TAIL : ((RULE_DIGIT|'-'|'_'|RULE_ASTERISK|RULE_PLUSSIGN|RULE_QUESTIONMARK) (RULE_DIGIT|RULE_UPPER|RULE_LOWER)*)?;

<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>

//${embeddedGrammar.name?upper_case} terminal fragments

<#list embeddedGrammar.terminalFragmentRules as terminalFragmentRule>
fragment RULE_${embeddedGrammar.name?upper_case}_TERMINAL_${terminalFragmentRule.terminalName?upper_case} : ${terminalFragmentRule.rule};
</#list>

//${embeddedGrammar.name?upper_case} terminals

<#list embeddedGrammar.terminalRules as terminalRule>
RULE_${embeddedGrammar.name?upper_case}_TERMINAL_${terminalRule.terminalName?upper_case} : { getCurrentState() == State.EMBEDDED_${embeddedGrammar.name?upper_case} }?=> ${terminalRule.rule} {expectEmbeddedMetaArg = false;};

</#list>

    <#list embeddedGrammar.nonterminals as nonterminal>
RULE_EMBEDDED_${embeddedGrammar.name?upper_case}_INJECT_${nonterminal.name?upper_case}: { getCurrentState() == State.EMBEDDED_${embeddedGrammar.name?upper_case} }?=> '!${nonterminal.name}' {expectEmbeddedMetaArg = false;};
    </#list>

    <#list embeddedGrammar.nonterminals as nonterminal>
RULE_EMBEDDED_${embeddedGrammar.name?upper_case}_META_${nonterminal.name?upper_case}: { getCurrentState() == State.EMBEDDED_${embeddedGrammar.name?upper_case} }?=> '#${nonterminal.name}' RULE_META_TAIL {expectEmbeddedMetaArg = true;};
    </#list>
    </#list>
</#if>

RULE_TEXT_TOKEN :  { (getCurrentState() == State.EMBEDDED_TEXT) 
                 || (getCurrentState() == State.EMBEDDED_OTHER) }?=> ~(('\u27E6'|'\u27E8'|'\u27EA'|'\u2983'|'\u2308'|'\u230A'|'\u2768'|'\u00AB'|'\u2039'|'\u29FC'|'\u2018'|'\u27E7'|'\u27E9'|'\u27EB'|'\u2984'|'\u2309'|'\u230B'|'\u2769'|'\u00BB'|'\u203A'|'\u29FD'|'\u2019'|'\u201C'|'\u201D'))+;

RULE_TEXT_ESCAPED_TOKEN :  { (getCurrentState() == State.EMBEDDED_TEXT) }?=>'\u201C' ( options {greedy=false;} : . )*'\u201D';



