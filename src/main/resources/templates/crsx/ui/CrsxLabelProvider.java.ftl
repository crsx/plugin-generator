/*
* generated by Xtext
*/
package ${package}.ui.labeling;

import ${package}.Utils;
import ${package}.crsx.Constructor;
import ${package}.crsx.Declaration;
import ${package}.crsx.Term;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

<#if embeddedGrammars??>
    <#list embeddedGrammars as embeddedGrammar>
        <#list embeddedGrammar.outlineElements as outlineElement>
import ${package}.crsx.${outlineElement.classname};
        </#list>
    </#list>
</#if>

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class CrsxLabelProvider extends DefaultEObjectLabelProvider {
    
    static final int OUTLINE_LABEL_TERM_MAX_CHARS = 10;

    @Inject
    public CrsxLabelProvider(AdapterFactoryLabelProvider delegate) {
        super(delegate);
    }
    
    /**
     * Truncate input string
     * 
     * Truncate input string if it is longer then maxLength character
     * and return truncated string with appendStr appended to the end of it 
     * 
     * @param str           input string
     * @param maxLength     maximal result length
     * @param appendStr     string to append if input is truncated
     * @return              original string if it was short enough, truncated string otherwise
     */
    public static String truncateIfLong(String str,int maxLength, String appendStr){
        if(str.length() <= maxLength ){
            return str;
        }
        return String.format("%s...", str.substring(0,maxLength-appendStr.length()));
    }
    
    /**
     * Get first and only string literal argument of term
     * 
     * @param term      ecore term node
     * @return          string argument literal if exists or "?" string otherwise
     */
    private static String getFirstStringTermArgument(Term term){
        String result = "?";
        EList<Declaration> arguments = term.getArgs();
        if(arguments.size() != 1){
            return result;
        }
        
        Declaration firstTermDecl = arguments.get(0);
        Term firstTerm = firstTermDecl.getOptionOrTerm();
        if( firstTerm == null ){
            return result;
        }
        
        String literal = firstTerm.getLiteral();
        if(literal == null || literal.length() < 2){
            return result;
        }
        
        char firstChar = literal.charAt(0);
        char lastChar  = literal.charAt(literal.length()-1);
        
        if( (firstChar != '"' && firstChar != '\'') 
                || (lastChar != '"' && lastChar != '\'')){
            return result;
        }
        
        result = literal.substring(1,literal.length() - 1);
        return result;
    }

    /**
     * Provide label for term
     * 
     * @param term
     * @return
     */
    public String text(Term term){
        Utils.CrsxTermType termType = Utils.determineTermType(term);
        switch(termType){
            case DIRECTIVE_USE:
            {
                return String.format("Use %s", getFirstStringTermArgument(term));
            }
            case DIRECTIVE_ADD_GRAMMAR:
            {
                return String.format("AddGrammar %s", getFirstStringTermArgument(term));
            }
            case FUNCTION_OR_DATA:
            {
                Constructor c = term.getConstructor();
                ICompositeNode node = NodeModelUtils.getNode(c);
                return NodeModelUtils.getTokenText(node);
            }
            default:
                return Utils.getTermTypeName(termType);
        }
    }
    
    private static String ruleLabel(String left,String right){
        String leftTruncated = truncateIfLong(left, OUTLINE_LABEL_TERM_MAX_CHARS, "...");
        String rightTruncated = truncateIfLong(right, OUTLINE_LABEL_TERM_MAX_CHARS, "...");
        return String.format("%s → %s", leftTruncated,rightTruncated);
    }
    
    /**
     * Provide label for declaration node
     * 
     * @param declaration   ecore declaration node
     * @return              label for declaration
     */
    public String text(Declaration declaration){
        Utils.CrsxDeclarationType declarationType = Utils.determineDeclarationType(declaration);
        switch(declarationType){
            case RULE:
                {
                String left = Utils.namedOptionOrTermText(declaration);
                String right = Utils.functionSortResultType(declaration);
                return ruleLabel(left,right);
                }
            case DATA_SORT:
            case POLYMORPHIC_DATA_SORT:
                {
                String dataSortName = Utils.namedOptionOrTermText(declaration);
                return String.format("%s[...]", dataSortName);
                }
            case FUNCTION_SORT:
            case POLYMORPHIC_FUNCTION_SORT:
                {
                String resultSort = Utils.functionSortResultType(declaration);
                String functionName = Utils.functionSortName(declaration);
                return String.format("%s[...] :: %s", functionName,resultSort);
                }
            case NAMED_RULE:
                {
                    Constructor c = declaration.getOptionOrTerm().getConstructor();
                    String nameOfRule = "?";
                    if(c != null){
                        ICompositeNode node = NodeModelUtils.getNode(c);
                        nameOfRule = NodeModelUtils.getTokenText(node);
                    }else{
                        nameOfRule = declaration.getOptionOrTerm().getVariable();
                    }

                    if(!"-".equals(nameOfRule)){
                        return String.format("%s → ...", nameOfRule);
                    }else{
                        String left = Utils.namedRuleLeft(declaration);
                        String right = Utils.functionSortResultType(declaration);
                        return ruleLabel(left, right);
                    }
                }
            default:
                break;
        }
        return Utils.getDeclarationTypeName(Utils.determineDeclarationType(declaration));
    }
    
    /**
     * Provide icon for declaration 
     * 
     * @param declaration
     * @return
     */
    public String image(final Declaration declaration){
        Utils.CrsxDeclarationType declarationType = Utils.determineDeclarationType(declaration);
        switch(declarationType){
            case RULE:
            case NAMED_RULE:
                //return "right_arrow.gif";
                return (String)super.image(declaration);
            default:
                return (String)super.image(declaration);
        }
    }
    
    protected static String getTextForObject(Object o){
        if(o instanceof String){
            return (String)o;
        }else if(o instanceof EObject){
            ICompositeNode node = NodeModelUtils.getNode((EObject)o);
            return NodeModelUtils.getTokenText(node);
        }
        return "Unknown";
    }
    
<#if embeddedGrammars??>
    // Generated methods for embedded languages
    <#list embeddedGrammars as embeddedGrammar>
        <#list embeddedGrammar.outlineElements as outlineElement>
            <#if !outlineElement.hidden> 
    //${outlineElement.classname}
    public String text(${outlineElement.classname} element){
        <#if outlineElement.fmtArgumentsList?size gt 0>
            <#list outlineElement.fmtArgumentsList as arg>
        String arg${arg?counter} = getTextForObject(element.get${arg?cap_first}());
            </#list>
        String result = String.format("${outlineElement.javaFmtString}"<#list outlineElement.fmtArgumentsList as arg>,arg${arg?counter}</#list>);
        return result;
        <#else>
        return "${outlineElement.javaFmtString}";
        </#if>
    }
            <#else>
   //${outlineElement.classname} hidden
            </#if>
        </#list>
    </#list>
</#if>
    
}
