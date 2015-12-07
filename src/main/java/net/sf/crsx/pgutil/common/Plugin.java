/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.util.zip.ZipOutputStream;

public abstract class Plugin {
    
    /**
     * Transforms qualified name of the resource to file path
     * 
     * This is done just by replacing all dots with / characters
     * 
     * @param qualifiedName
     * @return File with path coresponding to qualifiedName
     */
    protected static File getDirectoriesFromQualifiedName(String qualifiedName) {
        String pathStr = qualifiedName.replace(".", "/");
        return new File(pathStr);
    }
    
    public abstract void generatePluginSources(File root);
    
    public abstract void generatePluginSources(ZipOutputStream zip);
    
    public abstract File getParentPOMFile();
    
    public abstract File getEclipseRepoPath();
    
    public abstract String getPluginPackage();
    
    public abstract String getPluginName();
}
