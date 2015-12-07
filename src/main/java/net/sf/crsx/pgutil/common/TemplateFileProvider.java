/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.crsx.pgutil.PGToXtext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class TemplateFileProvider extends FileProvider{
    
    private static final String TEMPLATE_LOCATION = "templates/";
    
    private static final Configuration cfg;
    private static final Logger LOGGER;
    
    String templatePath;
    TemplateDataProvider dataProvider;
    Template template;
    
    static{
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassLoaderForTemplateLoading(PGToXtext.class.getClassLoader(), TEMPLATE_LOCATION);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        
        LOGGER = Logger.getGlobal();
    }
    
    public TemplateFileProvider(String templatePath,TemplateDataProvider dataProvider){
        this.templatePath = templatePath;
        this.dataProvider = dataProvider;
        this.template = null;
    }
    
    @Override
    public void writeFile(File file){
        
        if( template == null ){
            try{
                template = cfg.getTemplate(templatePath);
            }catch(IOException ioe){
                String exceptionMessage = String.format("Error loading template %s", templatePath);
                LOGGER.log(Level.SEVERE, exceptionMessage, ioe);
                return;
            }
        }
        
        Writer outputStream;
        
        try{
            outputStream = new OutputStreamWriter(new FileOutputStream(file,false));
        }catch(FileNotFoundException exception){
            String exceptionMessage = String.format("File %s not found", file.getAbsolutePath());
            LOGGER.log(Level.SEVERE, exceptionMessage, exception);
            return;
        }
        try{
            template.process(dataProvider.getData(), outputStream);
            outputStream.close();
        }catch(IOException ioe){
            String exceptionMessage = String.format("IO Error attempting to write file %s", file.getAbsolutePath());
            LOGGER.log(Level.SEVERE, exceptionMessage, ioe);
        }catch(TemplateException exception){
            String exceptionMessage = String.format("Template error generating %s file", file.getAbsolutePath());
            LOGGER.log(Level.SEVERE, exceptionMessage, exception);
        }
    }
    
    @Override
    public void writeFile(File file, ZipOutputStream zip){
        if( template == null ){
            try{
                template = cfg.getTemplate(templatePath);
            }catch(IOException ioe){
                String exceptionMessage = String.format("Error loading template %s", templatePath);
                LOGGER.log(Level.SEVERE, exceptionMessage, ioe);
                return;
            }
        }
        
        Writer outputStream;
        
        ZipEntry zipEntry = new ZipEntry(file.getPath());
        try {
            zip.putNextEntry(zipEntry);
        } catch (IOException exception) {
            String errorMessage = String.format("Error message creating zip entry %s", file.getPath());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }
        
        outputStream = new OutputStreamWriter(zip);
        
        try{
            template.process(dataProvider.getData(), outputStream);
        }catch(IOException ioe){
            String exceptionMessage = String.format("IO Error attempting to write file %s", file.getAbsolutePath());
            LOGGER.log(Level.SEVERE, exceptionMessage, ioe);
        }catch(TemplateException exception){
            String exceptionMessage = String.format("Template error generating %s file", file.getAbsolutePath());
            LOGGER.log(Level.SEVERE, exceptionMessage, exception);
        }
    }
    
    public interface TemplateDataProvider {
        
        public Object getData();
        
    }
}
