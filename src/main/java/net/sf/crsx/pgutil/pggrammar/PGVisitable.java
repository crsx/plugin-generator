/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.pggrammar;

public interface PGVisitable {

    public void visit( PGElementVisitor visitor );

}
