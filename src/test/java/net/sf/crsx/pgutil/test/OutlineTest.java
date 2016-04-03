package net.sf.crsx.pgutil.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
import net.sf.crsx.pgutil.PGToXtext;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGOutlineHints;
import net.sf.crsx.pgutil.pggrammar.PGOutlineHints.PGOutlineHint;
import net.sf.crsx.pgutil.xtextgen.XtextGrammarGenerator;

public class OutlineTest extends TestCase{

    protected static final String TEST_GRAMMAR_1 = 
            "grammar net.sf.testgrammar.TEST: <program>,<declaration>\n" +
            "\n" +
            "prefix t\n" +
            "\n" +
            "<program> ::= (<declaration>* <statement>)? .\n" +
            "<declaration> ::= <var_declaration> | <const_declaration>.\n" +
            "<var_declaration> ::=  /@ outline \"var decl {1}\" @/ 'var' identifier ';' ." +
            "<const_declaration> ::= /@ outline \"const decl\" @/ 'const' identifier '=' number ';'." +
            "\n" +
            "identifier ::= [a-zA-Z] [a-zA-Z0-9]*." +
            "number ::= '-'? [0-9]+." +
            "\n" +
            "skip ::= [ \t\n]+ | '{' ~('{'|'}') '}'.";
    
    PGGrammar grammar;
    PGOutlineHints outlineHints;
    Map<String,PGOutlineHint> hintMap;
    
    PGOutlineHint constDeclarationHint;
    PGOutlineHint varDeclarationHint;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        InputStream is = new ByteArrayInputStream(TEST_GRAMMAR_1.getBytes(StandardCharsets.UTF_8));
        
        grammar = PGToXtext.parseGrammar(is);
        
        if( grammar == null ){
            return;
        }
        
        //Outline hints are currently generated by Xtext grammar genenerator, so 
        //XtextGrammar generator must be invoked on grammar
        XtextGrammarGenerator grammarGenerator = new XtextGrammarGenerator();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        grammarGenerator.generateXtextGrammar(grammar, ps);
        
        outlineHints = grammar.getOutlineHints();
        if(outlineHints == null){
            return;
        }
        
        hintMap = outlineHints.getOutlineHintMap();
        if(hintMap == null){
            return;
        }
        
        constDeclarationHint = hintMap.get("const_declarationBase");
        varDeclarationHint = hintMap.get("var_declarationBase");
    }
    
    @Test
    public void testConstDeclarationMembers(){
        List<String> expected =
                Arrays.asList(null,"mIdentifier",null,"mNumber",null);
        Assert.assertEquals(expected, constDeclarationHint.getMemberList());
    }
    
    @Test
    public void testVarDeclarationMembers(){
        List<String> expected = 
                Arrays.asList(null,"mIdentifier",null);
        Assert.assertEquals(expected,varDeclarationHint.getMemberList());
    }
}