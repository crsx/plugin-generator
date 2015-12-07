package ${package}.ui.syntaxcoloring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class CrsxHighlightingConfiguration extends
		DefaultHighlightingConfiguration {

	public static final String METAVAR_ID = "Meta variable";
	public static final String FUNCTION_SORT_ID = "Function sort";
	public static final String DATA_SORT_ID = "Data sort";
	public static final String FUNCTION_ID = "Function";
	public static final String DATA_ID = "Data";
	public static final String PRIMITIVE_ID = "Primitive";
<#if embeddedGrammars??>
<#list embeddedGrammars as embeddedGrammar>
<#assign grammarNameUpper = embeddedGrammar.name?upper_case>
    public static final String EMBEDDED_${grammarNameUpper}_METAVAR_ID = "${embeddedGrammar.name} meta variable";
    public static final String EMBEDDED_${grammarNameUpper}_INJECT_ID = "${embeddedGrammar.name} inject";
    public static final String EMBEDDED_${grammarNameUpper}_KEYWORD_ID = "${embeddedGrammar.name} keyword";
</#list>
</#if>

	public CrsxHighlightingConfiguration() {
		super();
	}

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(METAVAR_ID, METAVAR_ID,
				metaTextStyle());
		acceptor.acceptDefaultHighlighting(FUNCTION_SORT_ID, FUNCTION_SORT_ID,
				functionSortTextStyle());
		acceptor.acceptDefaultHighlighting(DATA_SORT_ID, DATA_SORT_ID,
				dataSortTextStyle());
		acceptor.acceptDefaultHighlighting(PRIMITIVE_ID, PRIMITIVE_ID,
                primitiveTextStyle());
<#if embeddedGrammars??>
<#list embeddedGrammars as embeddedGrammar>
<#assign grammarNameUpper = embeddedGrammar.name?upper_case>
        acceptor.acceptDefaultHighlighting(EMBEDDED_${grammarNameUpper}_METAVAR_ID,EMBEDDED_${grammarNameUpper}_METAVAR_ID,
            embedded${grammarNameUpper}MetaTextStyle());
        acceptor.acceptDefaultHighlighting(EMBEDDED_${grammarNameUpper}_INJECT_ID,EMBEDDED_${grammarNameUpper}_INJECT_ID,
            embedded${grammarNameUpper}InjectTextStyle());
        acceptor.acceptDefaultHighlighting(EMBEDDED_${grammarNameUpper}_KEYWORD_ID,EMBEDDED_${grammarNameUpper}_KEYWORD_ID,
            embedded${grammarNameUpper}KeywordTextStyle());
</#list>
</#if>
	}

	private TextStyle primitiveTextStyle() {
		return keywordTextStyle();
	}

	public TextStyle metaTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(22, 109, 199));
		return textStyle;
	}

	public TextStyle functionSortTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(96, 0, 211));
		return textStyle;
	}

	public TextStyle dataSortTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	public TextStyle functionTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(96, 0, 211));
		return textStyle;
	}

	public TextStyle dataTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

<#if embeddedGrammars??>
<#list embeddedGrammars as embeddedGrammar>
<#assign grammarNameUpper = embeddedGrammar.name?upper_case>
    public TextStyle embedded${grammarNameUpper}MetaTextStyle() {
        return metaTextStyle().copy();
    }
    
    public TextStyle embedded${grammarNameUpper}InjectTextStyle() {
        TextStyle textStyle = defaultTextStyle().copy();
        textStyle.setColor(new RGB(204,0,0));
        return textStyle;
    }
    
    public TextStyle embedded${grammarNameUpper}KeywordTextStyle() {
        TextStyle textStyle = defaultTextStyle().copy();
        textStyle.setStyle(SWT.BOLD);
        textStyle.setColor(new RGB(0,204,0));
        return textStyle;
    }

</#list>
</#if>
}
