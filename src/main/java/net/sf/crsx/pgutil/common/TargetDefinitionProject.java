/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import net.sf.crsx.pgutil.Arguments;

public class TargetDefinitionProject extends Project{
    
    public TargetDefinitionProject(String projectName,Project baseProject,Arguments args){
        super(projectName);
        initTargetDefinitionProject(baseProject,args);
    }

    private void initTargetDefinitionProject(Project baseProject,
            Arguments args) {
        
        
        
        File projectRootDir = getProjectDirectory();
        
        TargetDataProvider targetDataProvider = new TargetDataProvider(baseProject.getProjectName(),args);
        
        //Add pom.xml file to target project
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("common/target/pom.xml.ftl",
                targetDataProvider);
        addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        //Add target file to target project
        String targetFilename = String.format("%s.target",getProjectName());
        File targetFile = new File(projectRootDir,targetFilename);
        FileProvider targetFileProvider;
        String customFilePath = args.getTargetDefinitionFile();
        if(customFilePath == null){
            targetFileProvider = new TemplateFileProvider("common/target/target.ftl",
                    targetDataProvider);
        }else{
            targetFileProvider = new CopyFileProvider(customFilePath);
        }
        addFileCreateTask(targetFile, targetFileProvider);
    }
    
    class TargetDataProvider implements TemplateFileProvider.TemplateDataProvider{
        
        Map<String,Object> data;
        
        TargetDataProvider(String pkg,Arguments args){
            
            data = new TreeMap<String,Object>();
            data.put("arguments", args);
            data.put("package", pkg);
        }
        
        @Override
        public Object getData() {
              return data;
        }
        
    }
}
