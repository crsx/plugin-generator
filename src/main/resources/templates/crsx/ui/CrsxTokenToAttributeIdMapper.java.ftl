package ${package}.ui.syntaxcoloring;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;

/**
 * 
 * @author villardl
 */
public class CrsxTokenToAttributeIdMapper extends
		DefaultAntlrTokenToAttributeIdMapper {

<#if embeddedGrammars??>
<#list embeddedGrammars as embeddedGrammar>
        static final Set<String> ${embeddedGrammar.name?lower_case}Keywords;
</#list>

        static{
<#list embeddedGrammars as embeddedGrammar>
            ${embeddedGrammar.name?lower_case}Keywords = new TreeSet<String>();
    <#list embeddedGrammar.keywords as keyword>
            ${embeddedGrammar.name?lower_case}Keywords.add("'${keyword.value}'");
    </#list>
</#list>
        }
</#if>

	public CrsxTokenToAttributeIdMapper() {
		super();
	}

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if (tokenName.equals("RULE_XML_COMMENT")){
			return DefaultHighlightingConfiguration.COMMENT_ID;
		}
		if (tokenName.equals("RULE_METAVARIABLE")){
			return CrsxHighlightingConfiguration.METAVAR_ID;
		}
<#if embeddedGrammars??>
<#list embeddedGrammars as embeddedGrammar>
<#assign grammarNameUpper = embeddedGrammar.name?upper_case>
        if (tokenName.startsWith("RULE_EMBEDDED_${grammarNameUpper}_META")){
            return CrsxHighlightingConfiguration.EMBEDDED_${grammarNameUpper}_METAVAR_ID;
        }
        if (tokenName.startsWith("RULE_EMBEDDED_${grammarNameUpper}_INJECT")){
            return CrsxHighlightingConfiguration.EMBEDDED_${grammarNameUpper}_INJECT_ID;
        }
        if(${embeddedGrammar.name?lower_case}Keywords.contains(tokenName)){
            return CrsxHighlightingConfiguration.EMBEDDED_${grammarNameUpper}_KEYWORD_ID;
        }
</#list>
</#if>
		return super.calculateId(tokenName, tokenType);
	}

}
