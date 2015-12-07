/* Copyright (c) 2007,2008,2015 IBM Corporation. */

package net.sf.crsx.pgutil.pggrammar;

/**
 * Helper structure to capture usage configuration.
 * @author <a href="mailto:krisrose@us.ibm.com">Kristoffer Rose</a>
 */
public class Usage
{
    /** True when <em>any</em> usage markers were present. */
    private boolean touched;

    private boolean definition, buffer, use, construct, embedded, literal;
    
    private boolean freeVar, freshVar, meta, wasConverted, needst, linear;
    private String name, converted, category;
    
    public boolean isTouched() {
        return touched;
    }
    public void setTouched(boolean touched) {
        this.touched = touched;
    }
    public boolean isDefinition() {
        return definition;
    }
    public void setDefinition(boolean definition) {
        this.definition = definition;
    }
    public boolean isBuffer() {
        return buffer;
    }
    public void setBuffer(boolean buffer) {
        this.buffer = buffer;
    }
    public boolean isUse() {
        return use;
    }
    public void setUse(boolean use) {
        this.use = use;
    }
    public boolean isConstruct() {
        return construct;
    }
    public void setConstruct(boolean construct) {
        this.construct = construct;
    }
    public boolean isEmbedded() {
        return embedded;
    }
    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }
    public boolean isLiteral() {
        return literal;
    }
    public void setLiteral(boolean literal) {
        this.literal = literal;
    }
    public boolean isFreeVar() {
        return freeVar;
    }
    public void setFreeVar(boolean freeVar) {
        this.freeVar = freeVar;
    }
    public boolean isFreshVar() {
        return freshVar;
    }
    public void setFreshVar(boolean freshVar) {
        this.freshVar = freshVar;
    }
    public boolean isMeta() {
        return meta;
    }
    public void setMeta(boolean meta) {
        this.meta = meta;
    }
    public boolean wasConverted() {
        return wasConverted;
    }
    public void setWasConverted(boolean wasConverted) {
        this.wasConverted = wasConverted;
    }
    public boolean isNeedst() {
        return needst;
    }
    public void setNeedst(boolean needst) {
        this.needst = needst;
    }
    public boolean isLinear() {
        return linear;
    }
    public void setLinear(boolean linear) {
        this.linear = linear;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getConverted() {
        return converted;
    }
    public void setConverted(String converted) {
        this.converted = converted;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
