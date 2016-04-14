/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PGGrammar implements PGVisitable {
    
    String className;
    String mainPackage;
    String prefix;
    Map<String,PGNonTerminal> nonTerminals;
    List<PGNonTerminal> nonterminalList;
    Map<String,PGTerminal> terminals;
    List<String> imports;
    List<String> declarations;
    Map<String,String> parserOptions;
    PGNonTerminal defaultExported;
    PGNonTerminal startNonterminal;
    
    PGTerminal commentTerminal;
    
    PGMeta meta;
    PGInject inject;
    
    boolean metaCreated;
    boolean injectCreated;
    
    String extension;
    
    PGOutlineHints outlineHints;

    /**
     * Creates new PG grammar object
     * 
     * @param className grammar class name
     */
    public PGGrammar(String className){
        this.className = className;
        nonTerminals = new TreeMap<String,PGNonTerminal>();
        imports = new ArrayList<String>();
        declarations = new ArrayList<String>();
        parserOptions = new TreeMap<String,String>();
        terminals = new TreeMap<String,PGTerminal>();
        metaCreated = false;
        injectCreated = false;
        outlineHints = new PGOutlineHints();
        nonterminalList = new ArrayList<PGNonTerminal>();
    }
    
    public String getGrammarClassName(){
        return className;
    }
    
    public String getGrammarPackage(){
        if( mainPackage == null ){
            int lastDot = className.lastIndexOf('.');
            mainPackage = className.substring(0, lastDot);
        }
        return mainPackage;
    }
    
    public boolean hasMeta(){
        return meta != null;
    }
    
    public void setMeta(PGMeta meta){
        this.meta = meta;
    }
    
    public PGMeta getMeta(){
        return meta;
    }
    
    public boolean hasInject(){
        return inject != null;
    }
    
    public void setInject(PGInject inject){
        this.inject = inject;
    }
    
    public PGInject getInject(){
        return inject;
    }
    
    public String getPrefix(){
        return prefix;
    }
    
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    
    public PGNonTerminal getStartNonTerminal(){
        return startNonterminal;
    }
    
    public void setStartNonTerminal(PGNonTerminal nonterminal){
        startNonterminal = nonterminal;
    }
    
    public PGTerminal getCommentTerminal(){
        return commentTerminal;
    }
    
    public void setCommentTerminal(PGTerminal commentTerminal){
        this.commentTerminal = commentTerminal;
    }
    
    public boolean isCommentTerminalSet(){
        return commentTerminal != null;
    }
    
    public String getExtension() {
        if(extension == null){
            extension = getName().toLowerCase();
        }
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public boolean guessStartingNonTerminal(){
        int unusedCount = 0;
        
        for( PGNonTerminal nonterminal : nonTerminals.values() ){
            if ( !nonterminal.used ){
                ++unusedCount;
                startNonterminal = nonterminal;
            }
        }
        return unusedCount == 1;
    }
    
    public void addTerminal(PGTerminal terminal){
        //TODO better handling of skip terminal
        if( "SKIP".equalsIgnoreCase(terminal.getName()) ){
            List<PGChoice> alternatives = terminal.getAlternative().getAlternatives();
            
            PGAlternative commentAlternative = new PGAlternative();
            PGAlternative skipAlternative = new PGAlternative();
            
            for( PGChoice choice : alternatives){
                List<PGElement> elements = choice.getElements();
                if( elements.size() == 1 ){
                    skipAlternative.addAlternative(choice);
                }else{
                    commentAlternative.addAlternative(choice);
                }
            }
            
            if(!commentAlternative.getAlternatives().isEmpty()){
                terminal.setAlternative(skipAlternative);
                commentTerminal = new PGTerminal("COMMENT");
                commentTerminal.setAlternative(commentAlternative);
                terminals.put(commentTerminal.getName(), commentTerminal);
            }
        }
            terminals.put(terminal.getName(), terminal);
    }
    
    public void addExportedNonTerminal(PGNonTerminal exported){
        PGNonTerminal nonTerminal;
        if( nonTerminals.containsKey(exported.getName()) ){
            nonTerminal = nonTerminals.get(exported.getName());
            nonTerminal.setExportedFlags(
                    (byte) (nonTerminal.getExportedFlags() | exported.getExportedFlags()) );
        }else{
            exported.setExported(true);
            nonTerminals.put(exported.getName(), exported);
        }
    }
    
    public void addToNonterminalList(PGNonTerminal nonTerminal){
        nonterminalList.add(nonTerminal);
    }
    
    public void addNonTerminal(PGNonTerminal nonTerminal){
        nonTerminals.put(nonTerminal.getName(), nonTerminal);
        //nonterminalList.add(nonTerminal);
    }
    
    public List<PGNonTerminal> getExportedNonTerminals(){
        List<PGNonTerminal> exportedNonTerminals = new LinkedList<PGNonTerminal>();
        for( PGNonTerminal nonTerminal : nonTerminals.values() ){
            if( nonTerminal.isExported() ){
                exportedNonTerminals.add(nonTerminal);
            }
        }
        return exportedNonTerminals;
    }
    
    public Collection<PGNonTerminal> getNonTerminals(){
        if( startNonterminal == null){
            return nonTerminals.values();
        }else{
            List<PGNonTerminal> result = new ArrayList<PGNonTerminal>();
            result.add(startNonterminal);
            for( PGNonTerminal nonterminal : nonterminalList ){
                if(nonterminal.name.equals(startNonterminal.getName())){
                    continue;
                }else{
                    result.add(nonterminal);
                }
            }
            return result;
        }
    }
    
    public Collection<PGTerminal> getTerminals(){
        return terminals.values();
    }
    
    public void setDefaultExportedNonTerminal(PGNonTerminal nonTerminal){
        defaultExported = nonTerminal;
    }
    
    public PGNonTerminal getDefaultExportedNonTerminal(){
        return defaultExported;
    }
    
    public void setParserOption(String key, String value){
        parserOptions.put(key, value);
    }
    
    public String getParserOption(String key){
        return parserOptions.get(key);
    }
    
    public void addImport(String imp){
        imports.add(imp);
    }
    
    public List<String> getImports(){
        return imports;
    }
    
    public void addDeclaration(String declaration){
        declarations.add(declaration);
    }
    
    public List<String> getDeclarations(){
        return declarations;
    }
    
    public PGNonTerminal getNonTerminal(String name){
        if( !nonTerminals.containsKey(name)){
            return null;
        }
        return nonTerminals.get(name);
    }
    
    public String getName(){
        if(!className.contains(".")){
            return className;
        }
        
        String [] parts = className.split("\\.");
        return parts[parts.length-1];
    }
    
    public PGOutlineHints getOutlineHints(){
        return outlineHints;
    }
    
    public void setOutlineHints(PGOutlineHints outlineHints){
        this.outlineHints = outlineHints;
    }

    public void addInjectRules(){
        if(injectCreated){
            return;
        }
        String grammarNameUpper = getName().toUpperCase();
        for( PGNonTerminal nonterminal : nonTerminals.values()){
            PGChoice choice = new PGChoice();
            PGArtificial artificial = new PGArtificial(String.format("{%sInject%s} LPAR EMBEDDED_%s_INJECT_%s cardinality=(QUESTIONMARK|PLUSSIGN|ASTERISK)? content=EmbeddedEmbedded RPAR",
                prefix,nonterminal.getName(),grammarNameUpper,nonterminal.getName().toUpperCase()));
            choice.addElement(artificial);
            nonterminal.addAlternative(choice);
        }
        injectCreated = true;
    }
    
    public void addMetaRules(){
        if(metaCreated){
            return;
        }
        String grammarNameUpper = getName().toUpperCase();
        for( PGNonTerminal nonterminal : nonTerminals.values()){
                PGChoice choice = new PGChoice();
                PGArtificial artificial = new PGArtificial(
                        String.format("{%sMeta%s} content=EMBEDDED_%s_META_%s (LSQUARE (args+=LID (COMMA args+=LID)*)? RSQUARE)?",
                        prefix,nonterminal.getName(),grammarNameUpper,nonterminal.getName().toUpperCase()));
                choice.addElement(artificial);
                nonterminal.addAlternative(choice);
        }
        metaCreated = true;
    }
    
    @Override
    public void visit(PGElementVisitor visitor) {
        visitor.visit(this);
    }

}
