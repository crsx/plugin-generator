/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Project {
    private static final Logger LOGGER = Logger.getGlobal();
    
    String projectName;
    List<File> directories;
    List<FileCreateTask> files;
    File projectRootDirectory;
    
    public Project(String projectName){
        this.projectName = projectName;
        directories = new ArrayList<File>();
        projectRootDirectory = new File(projectName);
        directories.add(projectRootDirectory);
        
        files = new ArrayList<FileCreateTask>();
    }
    
    public String getProjectName(){
        return projectName;
    }
    
    public File getProjectDirectory(){
        return projectRootDirectory;
    }
    
    public void addDirectory(File directory){
        directories.add(directory);
    }
    
    public void addFileCreateTask(File file, FileProvider fileProvider){
        files.add( new FileCreateTask(file,fileProvider));
    }
    
    public List<File> getDirectoryList(){
        return directories;
    }
    
    public boolean createDirectories(ZipOutputStream zip){
        for( File dir : directories ){
            String dirPath = dir.getPath();
            if (!dirPath.endsWith("/")){
                dirPath = String.format("%s/",dirPath);
            }
            
            ZipEntry zipDirEntry = new ZipEntry(dirPath);
            try {
                zip.putNextEntry(zipDirEntry);
                String msg = String.format("Directory %s created",dir.getPath());
                System.out.println(msg);
            } catch (IOException exception) {
                String errorMessage = String.format("Error creating directory entry for %s", dirPath );
                LOGGER.log(Level.SEVERE, errorMessage, exception);
            }
        }
        return true;
    }
    
    public boolean createDirectories(File root){
        for( File dir : directories ){
            File targetDir = new File(root,dir.getPath());
            if(!targetDir.isDirectory() && !targetDir.mkdirs()){
                String logMsg = String.format("Failed to create directory %s", dir.getPath());
                LOGGER.log(Level.SEVERE, logMsg);
                return false;
            }else{
                String msg = String.format("Directory %s created",dir.getPath());
                System.out.println(msg);
            }
        }
        return true;
    }
    
    public void writeFiles(ZipOutputStream zip){
        for( FileCreateTask fileTask : files){
            String msg = String.format("Generating file %s",fileTask.file.getPath());
            System.out.println(msg);
            fileTask.execute(zip);
        }
    }
    
    public void writeFiles(File root){
        for( FileCreateTask fileTask : files ){
            String msg = String.format("Generating file %s",fileTask.file.getPath());
            System.out.println(msg);
            fileTask.execute(root);
        }
    }
}