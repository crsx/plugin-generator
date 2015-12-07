/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CopyFileProvider extends FileProvider{
    
    private static final Logger LOGGER = Logger.getGlobal();
    
    String filePath;
    
    public CopyFileProvider(String filePath){
        this.filePath = filePath;
    }
    
    @Override
    public void writeFile(File file){
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException exception) {
            String errorMessage = String.format("File %s not found", filePath);
            LOGGER.log(Level.SEVERE, errorMessage,exception);
            return;
        }
        
        try{
            OutputStream os = new FileOutputStream(file);
            Util.copy(is, os);
            os.close();
        }catch(FileNotFoundException exception){
            String errorMessage = String.format("File %s not found",file.getName());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }catch(IOException exception){
            String errorMessage = String.format("IOException while copying %s", file.getAbsolutePath());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }
    }
    
    @Override
    public void writeFile(File file, ZipOutputStream zip){
        ZipEntry zipEntry = new ZipEntry(file.getPath());
        try {
            zip.putNextEntry(zipEntry);
        } catch (IOException exception) {
            String errorMessage = String.format("Error message creating zip entry %s", file.getPath());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }
        
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException exception) {
            String errorMessage = String.format("File %s not found", filePath);
            LOGGER.log(Level.SEVERE, errorMessage,exception);
            return;
        }
        try {
            Util.copy(is, zip);
        } catch (IOException exception) {
            String errorMessage = String.format("Error while writing %s to zip file", file.getPath());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }
    }
}
