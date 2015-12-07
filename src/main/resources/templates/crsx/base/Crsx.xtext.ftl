/* Copyright © 2012-2015 IBM Corporation. */
grammar net.sf.crsx.xtext.Crsx hidden(WS, ML_COMMENT, SL_COMMENT, XML_COMMENT) 

import "http://www.eclipse.org/emf/2002/Ecore" as ecore

generate crsx "http://www.sf.net/crsx/xtext/Crsx"
 
 
// Declarations

/**
 * A CRSX declaration can be:
 * 
 * Term                         (Term)
 * Term : Term                  (Typed Term)
 * 
 * Term → Term                  (Unnamed Rule)
 * Term : Term → Term           (Named Rule)
 *
 * Term :: Term                 (Function Sort)
 * ∀ x . Term :: Term           (Polymorphic Function Sort)
 *
 * Term ::=( Terms )            (Data Sort)
 * ∀ x . Term ::=( Terms )      (Polymorphic Data Sort)
 */
 Declaration:
    (POLY polyvars+=Variable+ DOT)?  optionOrTerm=Term 
        (
            sep=COLON left=Term (ARROW right=Term)? 
        |
            (sep=ARROW | sep=COLONCOLON) right=Term
        |
            sep=COLONCOLONEQ LPAR terms=Declarations? RPAR 
        )?
;  

Declarations returns Declarations :
    {Declarations} terms+=Declaration (SEMICOLON terms+=Declaration?)*
;
 
/**
 * A Term can be:
 * 
 * "String", Numeric                                (Literal)
 * {Properties}? (x y .)? x                         (Variable)
 * {Properties}? (x y .)? Constructor[ Terms ]      (Function/Data)
 * {Properties}? (x y .)? 'Constructor'[ Terms ]    (Function/Data)
 * {Properties}? (x y .)? MetaVariable[ Terms ]     (Pattern)
 * {Properties}? (x y .)? $Directive [ Terms ]      (Directive)
 * {Properties}? (x y .)? $[ Terms ]                (Builtin function)
 * {Properties}? (x y .)? %Category << Term >>      (Embdedded term) 
 * ( Terms )                                        (List of terms) 
 */ 
Term:
    {Term} 
    (
        literal=Literal
        | (properties+=Properties | =>binders+=Binders)*
            (   
                variable=Variable 
            |
                (
                    constructor=Constructor
                |
                    meta=METAVARIABLE
                |
                    directive=INTERNAL
                |
                    evaluator=EVALUATOR
                ) (LSQUARE (args+=Declaration (COMMA args+=Declaration)*)?  RSQUARE)?  
            | 
                embded=Embedded
            )
        | LPAR (terms+=Declaration (SEMICOLON terms+=Declaration?)*)? RPAR
    )
;

Binders:
    {Binders} binders+=Binder+ DOT
;

Binder:
        name=Variable (
            (COLON|COLONCOLON) 
            ( sort=(
                (
                    LID
                |
                    UID
                |
                    INTERNAL
                |
                    METAVARIABLE
                )) (LSQUARE (args+=Declaration (COMMA args+=Declaration)*)?  RSQUARE)?
            )
        )?
;


Properties:
    {Properties} LCURLY properties+=Property? (SEMICOLON properties+=Property?)* RCURLY
;
 
Property: 
    {Property} 
    (
        left=Term (COLON right=Term)? 
        | NOT notTerm=Term
    )
;

Embedded returns Embedded:
      (prefix=EMBEDDED_TEXT text=EmbeddedText)
<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
    <#assign grammarNameUpper = embeddedGrammar.name?upper_case>
    <#assign grammarNameLower = embeddedGrammar.name?lower_case>
    <#assign grammarDefaultNonterminal = embeddedGrammar.defaultNonTerminal>
    | (prefix=EMBEDDED_${grammarNameUpper} cardinality=(QUESTIONMARK|ASTERISK|PLUSSIGN)? ${grammarNameLower}content+=EMBEDDED_${grammarNameUpper}_CONTENT )
<#list embeddedGrammar.exportedNonTerminals as exportedNonterminal>
    | (prefix=EMBEDDED_${grammarNameUpper}_${exportedNonterminal.name?upper_case} cardinality=(QUESTIONMARK|ASTERISK|PLUSSIGN)? ${grammarNameLower}content+=EMBEDDED_${grammarNameUpper}_${exportedNonterminal.name?upper_case}_CONTENT )
</#list>
    </#list>
</#if>
    |   (
            prefix=EMBEDDED_OTHER 
          (
              ( LEMBEDDED3 content+=EmbeddedContent* REMBEDDED3 )
            | ( LEMBEDDED4 content+=EmbeddedContent* REMBEDDED4 )
            | ( LEMBEDDED5 content+=EmbeddedContent* REMBEDDED5 )
            | ( LEMBEDDED6 content+=EmbeddedContent* REMBEDDED6 )
            | ( LEMBEDDED7 content+=EmbeddedContent* REMBEDDED7 )
            | ( LEMBEDDED8 content+=EmbeddedContent* REMBEDDED8 )
            | ( LEMBEDDED9 content+=EmbeddedContent* REMBEDDED9 )
            | ( LEMBEDDED10 content+=EmbeddedContent* REMBEDDED10 )
            | ( LEMBEDDED11 content+=EmbeddedContent* REMBEDDED11 )
            | ( LEMBEDDED12 content+=EmbeddedContent* REMBEDDED12 )
            | ( LEMBEDDED13 content+=EmbeddedContent* REMBEDDED13 )
          )
        )
    
;

<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
    <#assign grammarNameUpper = embeddedGrammar.name?upper_case>
    <#assign grammarDefaultNonterminal= embeddedGrammar.defaultNonTerminal>
    <#assign var = grammarDefaultNonterminal.name?lower_case>
    <#if grammarDefaultNonterminal.exportedSingle>
EMBEDDED_${grammarNameUpper}_CONTENT returns Embedded${grammarNameUpper}${grammarDefaultNonterminal.name}:
              {Embedded${grammarNameUpper}${grammarDefaultNonterminal.name}}(( LEMBEDDED3 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED3 )
            | ( LEMBEDDED4 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED4 )
            | ( LEMBEDDED5 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED5 )
            | ( LEMBEDDED6 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED6 )
            | ( LEMBEDDED7 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED7 )
            | ( LEMBEDDED8 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED8 )
            | ( LEMBEDDED9 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED9 )
            | ( LEMBEDDED10 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED10 )
            | ( LEMBEDDED11 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED11 )
            | ( LEMBEDDED12 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED12 )
            | ( LEMBEDDED13 ${var}=${grammarNameUpper}_${grammarDefaultNonterminal.name} REMBEDDED13 ))
;
    <#else>
EMBEDDED_${grammarNameUpper}_CONTENT returns Embedded${grammarNameUpper}${grammarDefaultNonterminal.name}:
              {Embedded${grammarNameUpper}${grammarDefaultNonterminal.name}}(( LEMBEDDED3 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED3 )
            | ( LEMBEDDED4 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED4 )
            | ( LEMBEDDED5 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED5 )
            | ( LEMBEDDED6 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED6 )
            | ( LEMBEDDED7 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED7 )
            | ( LEMBEDDED8 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED8 )
            | ( LEMBEDDED9 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED9 )
            | ( LEMBEDDED10 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED10 )
            | ( LEMBEDDED11 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED11 )
            | ( LEMBEDDED12 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED12 )
            | ( LEMBEDDED13 ${var}+=${grammarNameUpper}_${grammarDefaultNonterminal.name}* REMBEDDED13 ))
;
    </#if>
    
    <#list embeddedGrammar.exportedNonTerminals as exportedNonterminal>
    <#assign var = exportedNonterminal.name?lower_case>
    <#if exportedNonterminal.exportedSingle>
EMBEDDED_${grammarNameUpper}_${exportedNonterminal.name?upper_case}_CONTENT returns Embedded${grammarNameUpper}${exportedNonterminal.name}:
              {Embedded${grammarNameUpper}${exportedNonterminal.name}}(( LEMBEDDED3 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED3 )
            | ( LEMBEDDED4 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED4 )
            | ( LEMBEDDED5 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED5 )
            | ( LEMBEDDED6 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED6 )
            | ( LEMBEDDED7 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED7 )
            | ( LEMBEDDED8 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED8 )
            | ( LEMBEDDED9 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED9 )
            | ( LEMBEDDED10 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED10 )
            | ( LEMBEDDED11 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED11 )
            | ( LEMBEDDED12 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED12 )
            | ( LEMBEDDED13 ${var}=${grammarNameUpper}_${exportedNonterminal.name} REMBEDDED13 ))
;
    <#else>
EMBEDDED_${grammarNameUpper}_${exportedNonterminal.name?upper_case}_CONTENT returns Embedded${grammarNameUpper}${exportedNonterminal.name}:
              {Embedded${grammarNameUpper}${exportedNonterminal.name}}(( LEMBEDDED3 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED3 )
            | ( LEMBEDDED4 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED4 )
            | ( LEMBEDDED5 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED5 )
            | ( LEMBEDDED6 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED6 )
            | ( LEMBEDDED7 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED7 )
            | ( LEMBEDDED8 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED8 )
            | ( LEMBEDDED9 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED9 )
            | ( LEMBEDDED10 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED10 )
            | ( LEMBEDDED11 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED11 )
            | ( LEMBEDDED12 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED12 )
            | ( LEMBEDDED13 ${var}+=${grammarNameUpper}_${exportedNonterminal.name}* REMBEDDED13 ))
;
    </#if>
    </#list>
    </#list>
</#if>

// Unknown Embedded content
EmbeddedContent:
    {EmbeddedContent} (
        content=(TEXT_TOKEN|TEXT_ESCAPED_TOKEN)
    |   
        embedded=EmbeddedEmbedded
    )
;
  
EmbeddedEmbedded:
    {EmbeddedEmbedded} (
        LEMBEDDED3 declaration=Declaration REMBEDDED3
    |   
        LEMBEDDED4 declaration=Declaration REMBEDDED4
    |
        LEMBEDDED5 declaration=Declaration REMBEDDED5
    |
        LEMBEDDED6 declaration=Declaration REMBEDDED6
    |
        LEMBEDDED7 declaration=Declaration REMBEDDED7
    |
        LEMBEDDED8 declaration=Declaration REMBEDDED8
    |
        LEMBEDDED9 declaration=Declaration REMBEDDED9
    |
        LEMBEDDED10 declaration=Declaration REMBEDDED10
    |
        LEMBEDDED11 declaration=Declaration REMBEDDED11
    |
        LEMBEDDED12 declaration=Declaration REMBEDDED12
    |
        LEMBEDDED13 declaration=Declaration REMBEDDED13
    )
;

// Text Mode

EmbeddedText :
    {EmbeddedText} (
        LEMBEDDED3 content+=EmbeddedTextContent* REMBEDDED3
    |   
        LEMBEDDED4 content+=EmbeddedTextContent* REMBEDDED4
    |
        LEMBEDDED5 content+=EmbeddedTextContent* REMBEDDED5
    |
        LEMBEDDED6 content+=EmbeddedTextContent* REMBEDDED6
    |
        LEMBEDDED7 content+=EmbeddedTextContent* REMBEDDED7
    |
        LEMBEDDED8 content+=EmbeddedTextContent* REMBEDDED8
    |
        LEMBEDDED9 content+=EmbeddedTextContent* REMBEDDED9
    |
        LEMBEDDED10 content+=EmbeddedTextContent* REMBEDDED10
    |
        LEMBEDDED11 content+=EmbeddedTextContent* REMBEDDED11
    |
        LEMBEDDED12 content+=EmbeddedTextContent* REMBEDDED12
    |
        LEMBEDDED13 content+=EmbeddedTextContent* REMBEDDED13
        
    )
;

EmbeddedTextContent:
    {EmbeddedContent} (
        text=TEXT_TOKEN
    |   
        LEMBEDDED3 econtent+=EmbeddedTextContent* REMBEDDED3 // Indent
    |   
        text=TEXT_ESCAPED_TOKEN
    |
        LEMBEDDED6 econtent+=EmbeddedTextContent* REMBEDDED6
    |
        embedded=EmbeddedTextEmbedded
    )
;

EmbeddedTextEmbedded:
    {EmbeddedEmbedded} (
        LEMBEDDED4 declaration=Declaration REMBEDDED4
    |
        LEMBEDDED5 declaration=Declaration REMBEDDED5
    |
        LEMBEDDED7 declaration=Declaration REMBEDDED7
    |
        LEMBEDDED8 declaration=Declaration REMBEDDED8
    |
        LEMBEDDED9 declaration=Declaration REMBEDDED9
    |
        LEMBEDDED10 declaration=Declaration REMBEDDED10
    |
        LEMBEDDED11 declaration=Declaration REMBEDDED11
    |
        LEMBEDDED12 declaration=Declaration REMBEDDED12
    |
        LEMBEDDED13 declaration=Declaration REMBEDDED13
    )
;

/** A function or data construction. When declarations is null, this is a data constructor */
Constructor:
    {Constructor} (declaration=[Declaration|ATOM] | declaration=[Declaration|UID] | declaration=[Declaration|COLON])
;

// Data Rules

Literal returns ecore::EString: 
    STRING | NUMERIC
;

Variable returns ecore::EString hidden():
    LID 
;

<#if embeddedGrammars??>
<#list embeddedGrammars as embeddedGrammar>
/*
 * ${embeddedGrammar.name?upper_case} grammar rules
 */

${embeddedGrammar.xtextGrammarRules}
</#list>
</#if>

/* All terminal definitions will be ignored for actual lexer 
 * (which is implemented in /src/net/sf/crsx/xtext/lexer/CrsxCustomLexer.g),
 * however definitions below are still required and the terminal names
 * must match with ones in CrsxCustomLexer.g 
 */
 
terminal LID            : LINEAR? LOWER (ALPHANUMERIC | OTHER | DASH | UNDERSCORE )* LINEAR? FUNCTIONAL?;
terminal UID            : ((UPPER|DASH|UNDERSCORE) (ALPHANUMERIC|DASH|UNDERSCORE)*) | ('@'|'^'|'*'|'+'|'`'|'|'| '#'|'/'|'='|'~')+;

terminal fragment LINEAR        : '¹';
terminal fragment FUNCTIONAL    : 'ᵇ';

terminal INTERNAL       : '$' (UID|LID);
terminal EVALUATOR      : '$';
terminal DASH           :'-';
terminal METAVARIABLE   : '#' (LOWER | UPPER | DIGIT | OTHER | DASH | UNDERSCORE | ASTERISK | QUESTIONMARK | PLUSSIGN )*; 

terminal NUMERIC        : (DIGIT)+ ("." DIGIT+)? (("E"|"e") DIGIT+)?;
terminal NOT            : '¬';

terminal ATOM           : "'" -> "'"; //: "'" ( ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|"'"|'\\')) | !('\\'|"'") )* "'";
terminal STRING         : '"' ( ('\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|"'"|'\\')) | !('\\'|'"') )* '"';
 
terminal ARROW          : '→';
terminal POLY           : '∀';
terminal DOT            : '.';
terminal ASTERISK       : '*';
terminal PLUSSIGN       : '+';
terminal QUESTIONMARK   : '?';
terminal EMBEDDED_TEXT  : '%n';
<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
terminal EMBEDDED_${embeddedGrammar.name?upper_case} : '%${embeddedGrammar.prefix}';
<#list embeddedGrammar.exportedNonTerminals as exportedNonTerminal>
terminal EMBEDDED_${embeddedGrammar.name?upper_case}_${exportedNonTerminal.name?upper_case} : '%${embeddedGrammar.prefix}${exportedNonTerminal.name}';
</#list>
    </#list>
</#if>
terminal EMBEDDED_OTHER : '%' !('n'<#if embeddedGrammars??><#list embeddedGrammars as embeddedGrammar>|'${embeddedGrammar.prefix}'</#list></#if>) (LOWER|UPPER|DIGIT|'_')* (QUESTIONMARK|ASTERISK|PLUSSIGN)?;

terminal COLONCOLONEQ   : '::=';
terminal COLONCOLON     : '::';
terminal COLON          : ':';
terminal SEMICOLON      : ';';
terminal COMMA          : ',';
terminal UNDERSCORE     :'_';

terminal LCURLY         : '{';
terminal RCURLY         : '}'; 

terminal LSQUARE        : '[';
terminal RSQUARE        : ']';

terminal LPAR           : '(';
terminal RPAR           : ')';

terminal LEMBEDDED3     : '⟦';
terminal LEMBEDDED4     : "⟨";
terminal LEMBEDDED5     : "⟪";
terminal LEMBEDDED6     : "⦃";
terminal LEMBEDDED7     : "⌈";
terminal LEMBEDDED8     : "⌊";
terminal LEMBEDDED9     : "❨";
terminal LEMBEDDED10    : '«';
terminal LEMBEDDED11    : '‹';
terminal LEMBEDDED12    : '⧼';
terminal LEMBEDDED13    : '‘';

terminal REMBEDDED3     : '⟧';
terminal REMBEDDED4     : "⟩";
terminal REMBEDDED5     : "⟫";
terminal REMBEDDED6     : "⦄";
terminal REMBEDDED7     : "⌉";
terminal REMBEDDED8     : "⌋";
terminal REMBEDDED9     : "❩";
terminal REMBEDDED10    : '»';
terminal REMBEDDED11    : '›';
terminal REMBEDDED12    : '⧽';
terminal REMBEDDED13    : '’';

terminal fragment START_XML      : '<!--';
terminal fragment END_XML        : '-->';

terminal ML_COMMENT     : '/*' -> '*/';
terminal XML_COMMENT    : START_XML -> END_XML;
terminal SL_COMMENT     : '//' !('\n'|'\r')* ('\r'? '\n')?;
terminal WS             : (' '|'\t'|'\r'|'\n')+;

terminal fragment ALPHANUMERIC      : UPPER | LOWER | DIGIT;
terminal fragment DIGIT             : '0'..'9';
terminal fragment UPPER             : 'A'..'Z'; //|"\u00C0"|"\u00D6"|"\u00D8".."\u00DE";
terminal fragment LOWER             : 'a'..'z'; // |"\u00DF".."\u00F6"|"\u00F8".."\u00FF"; 
terminal fragment OTHER             : '@'|'^'|'*'|'+'|'-'|'`'|'|'| '#'|'/'|'!'|'%'|'='|'~'|'\u2190'..'\u21ff';

<#if embeddedGrammars??>
terminal fragment META_TAIL         : ((DIGIT | '-' | '_' | '*' | '?' | '+') ( DIGIT | UPPER | LOWER )*)?;
<#list embeddedGrammars as embeddedGrammar>
/*
 * ${embeddedGrammar.name?upper_case} terminal rules
 */

${embeddedGrammar.xtextTerminalRules}

/*
 * ${embeddedGrammar.name?upper_case} inject terminal rules
 */

    <#list embeddedGrammar.nonterminals as nonTerminal>
terminal EMBEDDED_${embeddedGrammar.name?upper_case}_INJECT_${nonTerminal.name?upper_case}: '!${nonTerminal.name}' ;
    </#list>

/*
 * ${embeddedGrammar.name?upper_case} meta terminal rules
 */

    <#list embeddedGrammar.nonterminals as nonTerminal>
terminal EMBEDDED_${embeddedGrammar.name?upper_case}_META_${nonTerminal.name?upper_case}: '#${nonTerminal.name}' META_TAIL;
    </#list>
</#list>
</#if>

terminal TEXT_TOKEN: !('⟦'|"⟨"|"⟪"|"⦃"|"⌈"|"⌊"|"❨"|'«'|'‹'|'⧼'|'‘'|'⟧'|"⟩"|'⟫'|"⦄"|"⌉"|"⌋"|"❩"|'»'|'›'|'⧽'|'’'|'“'|'”')+;
terminal TEXT_ESCAPED_TOKEN: '“' -> '”';