/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DirectoryZipper {
    
    final File sourceDirectory;
    final Path sourcePath;
    final File zipFile;
    
    public DirectoryZipper(File source, File zipFile){
        sourceDirectory = source;
        sourcePath = sourceDirectory.toPath();
        this.zipFile = zipFile;
    }
    
    protected void processDirectory(File dir,ZipOutputStream zos) throws IOException{
        for( String filename : dir.list() ){
            File file = new File(dir,filename);
            if( file.isDirectory() ){
                
                String dirPath = sourcePath.relativize(file.toPath()).toString();
                
                if (!dirPath.endsWith("/")){
                    dirPath = String.format("%s/",dirPath);
                }
                ZipEntry zipDirEntry = new ZipEntry(dirPath);
                zos.putNextEntry(zipDirEntry);
                zos.closeEntry();
                
                processDirectory(file,zos);
            }else if(file.isFile()){
                processFile(file,zos);
            }
        }
    }
    
    protected void processFile(File file,ZipOutputStream zos) throws IOException{
        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(file.toPath()).toString());
        zos.putNextEntry(zipEntry);
        
        InputStream is = new FileInputStream(file);
        
        Util.copy(is, zos);
        zos.closeEntry();
    }
    
    public void process() throws IOException{
        if( ! sourceDirectory.isDirectory() ){
            String errMsg = String.format("%s is not directory", sourceDirectory.getAbsolutePath());
            throw new IOException(errMsg);
        }
        
        FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fileOutputStream);
        
        processDirectory( sourceDirectory, zos);
        
        zos.flush();
        zos.close();
    }
}
