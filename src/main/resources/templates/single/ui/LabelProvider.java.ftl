package ${package}.ui.labeling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

<#list outlineElements as outlineElement>
    <#if !outlineElement.hidden>
import ${package}.${name?lower_case}.${outlineElement.classname};
    </#if>
</#list>

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class ${name}LabelProvider extends DefaultEObjectLabelProvider {

    @Inject
    public ${name}LabelProvider(AdapterFactoryLabelProvider delegate) {
        super(delegate);
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

<#list outlineElements as outlineElement>
    <#if !outlineElement.hidden> 
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
    
    </#if>
</#list>
    
}