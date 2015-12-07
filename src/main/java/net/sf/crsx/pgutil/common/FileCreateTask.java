/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.util.zip.ZipOutputStream;

public class FileCreateTask{
    
    File file;
    FileProvider provider;
    
    public FileCreateTask(File file, FileProvider provider){
        this.file = file;
        this.provider = provider;
    }
    
    public void execute(ZipOutputStream zip){
        provider.writeFile(file,zip);
    }
    
    public void execute(File root){
        File targetFile = new File(root,file.getPath());
        provider.writeFile(targetFile);
    }
}