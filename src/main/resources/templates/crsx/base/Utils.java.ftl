/* Copyright © 2012-2015 IBM Corporation. */
package ${package};

import ${package}.crsx.Constructor;
import ${package}.crsx.Declaration;
import ${package}.crsx.Term;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * 
 * @author villardl
 */
public class Utils {
    
    public static final String CRSX_DIRECTIVE_USE = "$Use";
    public static final String CRSX_DIRECTIVE_ADD_GRAMMAR = "$AddGrammar";
    public static final String CRSX_DIRECTIVE_LAX = "$Lax";
    
    public static enum CrsxDeclarationType{
        FUNCTION_SORT,
        POLYMORPHIC_FUNCTION_SORT,
        DATA_SORT,
        POLYMORPHIC_DATA_SORT,
        RULE,
        NAMED_RULE,
        TERM,
        TYPED_TERM,
        GROUP
    };
    
    public static enum CrsxTermType{
        LITERAL,
        VARIABLE,
        FUNCTION_OR_DATA,
        PATTERN,
        DIRECTIVE_USE,
        DIRECTIVE_LAX,
        DIRECTIVE_ADD_GRAMMAR,
        DIRECTIVE_OTHER,
        BUILDIN_FUNCTION,
        EMBEDDED_TERM,
        LIST,
        TERM
    };
    
    /**
     * Provides text label for declaration type
     * 
     * @param declarationType
     * @return
     */
    public static String getDeclarationTypeName(CrsxDeclarationType declarationType){
        switch(declarationType){
            case FUNCTION_SORT:
                return "Function sort";
            case POLYMORPHIC_FUNCTION_SORT:
                return "Polymorphic function sort";
            case DATA_SORT:
                return "Data sort";
            case POLYMORPHIC_DATA_SORT:
                return "Polymorphic data sort";
            case RULE:
                return "Rule";
            case NAMED_RULE:
                return "Named rule";
            case TERM:
                return "Term declaration xx";
            case TYPED_TERM:
                return "Typed term";
            case GROUP:
                return "Group";
            default:
                return "Term declaration";
        }
    }
    
    /**
     * Provides text label for term type
     * 
     * @param termType
     * @return
     */
    public static String getTermTypeName(CrsxTermType termType){
        switch(termType){
            case LITERAL:
                return "Literal";
            case VARIABLE:
                return "Variable";
            case FUNCTION_OR_DATA:
                return "Function/Data term";
            case PATTERN:
                return "Pattern";
            case DIRECTIVE_USE:
                return "Use directive";
            case DIRECTIVE_OTHER:
                return "Directive";
            case BUILDIN_FUNCTION:
                return "Buildin function";
            case EMBEDDED_TERM:
                return "Embedded term";
            case LIST:
                return "List";
            case TERM:
            default:
                return "Term";
        }
    }
    
    /**
     * Determine type of declaration
     * 
     * @param declaration
     * @return
     */
    public static CrsxDeclarationType determineDeclarationType(final Declaration declaration){
        
        if( !declaration.getPolyvars().isEmpty() ){
            //Polymorphic function or data sort
            if( "::".equals(declaration.getSep()) ){
                return CrsxDeclarationType.POLYMORPHIC_FUNCTION_SORT;
            }else{
                return CrsxDeclarationType.POLYMORPHIC_DATA_SORT;
            }
        }else if( ":".equals(declaration.getSep()) ){
            //Typed term or named rule
            if( declaration.getRight() == null ){
                return CrsxDeclarationType.TYPED_TERM;
            }else{
                return CrsxDeclarationType.NAMED_RULE;
            }
        }else if( "::".equals(declaration.getSep())){
            return CrsxDeclarationType.FUNCTION_SORT;
        }else if("→".equals(declaration.getSep())){
            return CrsxDeclarationType.RULE;
        }else if("::=".equals(declaration.getSep())){
            return CrsxDeclarationType.DATA_SORT;
        }
        
        if(declaration.getLeft() == null &&
                declaration.getRight() == null &&
                declaration.getTerms() == null){
            
            Term term = declaration.getOptionOrTerm();
            
            Constructor constructor = term.getConstructor();
            if( constructor != null && term.getArgs().size() == 1 ){
                return CrsxDeclarationType.GROUP;
            }
        }
        
        return CrsxDeclarationType.TERM;
    }
    
    /**
     * Determines type of CRSX directive term
     * 
     * @param term      ecore Term node
     * @return          directive type if directive type can be determined
     */
    private static CrsxTermType determineDirectiveType(final Term term){
        String directive = term.getDirective();
        if( CRSX_DIRECTIVE_USE.equals(directive) ){
            return CrsxTermType.DIRECTIVE_USE;
        }else if( CRSX_DIRECTIVE_ADD_GRAMMAR.equals(directive)){
            return CrsxTermType.DIRECTIVE_ADD_GRAMMAR;
        }else if( CRSX_DIRECTIVE_LAX.equals(directive)){
            return CrsxTermType.DIRECTIVE_LAX;
        }
        return CrsxTermType.DIRECTIVE_OTHER;
    }
    
    /**
     * Determines type of term
     * 
     * @param term      ecore Term node
     * @return          type of CRSX term node or TERM otherwise
     */
    public static CrsxTermType determineTermType(final Term term){
        if(term.getLiteral() != null ){
            return CrsxTermType.LITERAL;
        }else if(term.getVariable() != null){
            return CrsxTermType.VARIABLE;
        }else if(term.getConstructor() != null){
            return CrsxTermType.FUNCTION_OR_DATA;
        }else if(term.getMeta() != null){
            return CrsxTermType.PATTERN;
        }else if(term.getDirective() != null){
            return determineDirectiveType(term);
        }else if(term.getEmbded() != null){
            return CrsxTermType.EMBEDDED_TERM;
        }else if(!term.getTerms().isEmpty()){
            return CrsxTermType.LIST;
        }
        return CrsxTermType.TERM;
    }
    
    /**
     * Whether the given object is a data sort
     * 
     * @param eobj
     * @return
     */
    public static boolean isFunctionSort(EObject eobj) {
        if (eobj instanceof Declaration) {
            final Declaration decl = (Declaration) eobj;
            return "::".equals(decl.getSep()) && decl.getOptionOrTerm() != null
                    && decl.getOptionOrTerm().getConstructor() != null;
        }
        return false;
    }

    /**
     * Gets the name of the function sort.
     * 
     * @param decl
     *            A function sort
     * @return
     */
    public static String functionSortName(Declaration decl) {
        Constructor c = decl.getOptionOrTerm().getConstructor();
        ICompositeNode node = NodeModelUtils.getNode(c);

        return NodeModelUtils.getTokenText(node);
    }
    
    /**
     * Returns Term text of declaration which means:
     * name of named rule if decl is named rule
     * name of function sort if decl is function sort/polymorphic function sort
     * 
     * @param decl Declaration
     * @return
     */
    public static String namedOptionOrTermText(Declaration decl) {
        ICompositeNode node = NodeModelUtils.getNode(decl.getOptionOrTerm());
        
        return NodeModelUtils.getTokenText(node);
    }

    /**
     * Whether the given object is a data sort
     * 
     * @param eobj
     * @return
     */
    public static boolean isDataSort(EObject eobj) {
        if (eobj instanceof Declaration) {
            final Declaration decl = (Declaration) eobj;
            return "::=".equals(decl.getSep()) && decl.getOptionOrTerm() != null
                    && decl.getOptionOrTerm().getConstructor() != null;
        }
        return false;
    }
    
    /**
     * Gets the name of the data sort.
     * 
     * @param decl
     *            A function sort
     * @return
     */
    public static String dataSortName(Declaration decl) {
        return functionSortName(decl);
    }
    
    /**
     * Gets result type of function sort
     * 
     * @param decl  function sort or polymorphic function sort
     * @return
     */
    public static String functionSortResultType(Declaration decl) {
        ICompositeNode node = NodeModelUtils.getNode(decl.getRight());
        
        return NodeModelUtils.getTokenText(node);

    }
    
    /**
     * Get text coresponding to Constructor AST element
     * 
     * @param c Constructor object
     * @return String coresponding to constructor
     */
    public static String getConstructorText(Constructor c){
        if(c == null){
            return "<unknown>";
        }
        
        ICompositeNode node = NodeModelUtils.getNode(c);
        return NodeModelUtils.getTokenText(node);
    }
    
    /**
     * Get function sort name of name rule
     * 
     * @param decl Declaration object which is named rule
     * @return Function sort name 
     */
    public static String namedRuleFunctionSortName(Declaration decl){
        return getConstructorText(decl.getLeft().getConstructor());
    }
    
    /**
     * Get function sort name of unnamed rule
     * 
     * @param decl Declaration object which is unnamed rule
     * @return Function sort name
     */
    public static String ruleFunctionSortName(Declaration decl){
        return getConstructorText(decl.getOptionOrTerm().getConstructor());
    }
    
    /**
     * Returns left side of named rule
     * 
     * @param decl  named rule
     * @return
     */
    public static String namedRuleLeft(Declaration decl) {
        ICompositeNode node = NodeModelUtils.getNode(decl.getLeft());
        
        return NodeModelUtils.getTokenText(node);
    }
    
    /**
     * Whether the given object is a primitive
     * 
     * @param eobj
     * @return
     */
    public static boolean isPrimitive(EObject eobj) {
        if (eobj instanceof Term) {
            final Term term = (Term) eobj;
            return term.getEvaluator() != null;
        }
        return false;
    }
    
    /**
     * Whether the given object is a variable
     * 
     * @param eobj
     * @return
     */
    public static boolean isVariable(EObject eobj) {
        if (eobj instanceof Term) {
            final Term term = (Term) eobj;
            return term.getVariable() != null;
        }
        return false;
    }
    
    
}
