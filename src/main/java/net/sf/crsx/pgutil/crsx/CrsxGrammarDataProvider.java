/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.crsx;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.crsx.pgutil.common.TemplateFileProvider;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;

public class CrsxGrammarDataProvider implements TemplateFileProvider.TemplateDataProvider{

    List<PGGrammar> grammars;
    
    Map<String,Object> data;
    
    public CrsxGrammarDataProvider(List<PGGrammar> grammars){
        this.grammars = grammars;
        
        data = new TreeMap<String,Object>();
    }
    
    @Override
    public Object getData() {
        return data;
    }

}
