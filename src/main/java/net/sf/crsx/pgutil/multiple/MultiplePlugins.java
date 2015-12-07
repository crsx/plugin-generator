/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.multiple;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

import net.sf.crsx.pgutil.Constants;
import net.sf.crsx.pgutil.PGToXtext;
import net.sf.crsx.pgutil.common.Plugin;
import net.sf.crsx.pgutil.common.Project;
import net.sf.crsx.pgutil.common.TemplateFileProvider;

public class MultiplePlugins extends Plugin{

    private List<Plugin> plugins;
    
    private Project parentProject;
    private Project updateSiteProject;
    
    private MultiplePluginDataProvider dataProvider;
    
    public MultiplePlugins(List<Plugin> plugins){
        this.plugins = plugins;
        
        String basePackageName = Constants.GENERATED_CRSX_PLUGIN_PACKAGE;
        String parentProjectName = String.format("%s.multiple.parent", basePackageName );
        parentProject = new Project(parentProjectName);
        
        String updateSiteProjectName = String.format("%s.multiple.updatesite", basePackageName );
        updateSiteProject = new Project(updateSiteProjectName);
        
        dataProvider = new MultiplePluginDataProvider(plugins);
        
        createStructureOfProjects();
    }
    
    private void createStructureOfProjects(){
        createParentProjectStructure();
        createUpdateSiteProjectStructure();
    }
    
    private void createParentProjectStructure(){
        //This is the root project of main directory
        File projectRootDir = parentProject.getProjectDirectory();
        
        //Add pom.xml
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("multiple/parent/pom.xml.ftl",
                dataProvider);
        parentProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
    }
    
    private void createUpdateSiteProjectStructure(){
        File projectRootDir = updateSiteProject.getProjectDirectory();
        
        //Add pom.xml
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("multiple/updatesite/pom.xml.ftl",
                dataProvider);
        updateSiteProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        //Add category.xml
        File categoryXMLFile = new File(projectRootDir,"category.xml");
        TemplateFileProvider categoryXMLFileProvider = new TemplateFileProvider("multiple/updatesite/category.xml.ftl",
                dataProvider);
        updateSiteProject.addFileCreateTask(categoryXMLFile, categoryXMLFileProvider);
    }
    
    @Override
    public void generatePluginSources(File root) {
        for(Plugin plugin : plugins){
            plugin.generatePluginSources(root);
        }
        
        parentProject.createDirectories(root);
        parentProject.writeFiles(root);
        
        updateSiteProject.createDirectories(root);
        updateSiteProject.writeFiles(root);
    }

    @Override
    public void generatePluginSources(ZipOutputStream zip) {
        for(Plugin plugin : plugins){
            plugin.generatePluginSources(zip);
        }
        
        parentProject.createDirectories(zip);
        parentProject.writeFiles(zip);
        
        updateSiteProject.createDirectories(zip);
        updateSiteProject.writeFiles(zip);
    }

    @Override
    public File getParentPOMFile() {
        return new File(parentProject.getProjectDirectory(),"pom.xml");
    }

    @Override
    public File getEclipseRepoPath() {
        return new File(updateSiteProject.getProjectDirectory(),"target/repository");
    }
    
    public static final class MultiplePluginDataProvider implements TemplateFileProvider.TemplateDataProvider {
        
        private Map<String,Object> data;
        
        public MultiplePluginDataProvider(List<Plugin> plugins){
            data = new TreeMap<String,Object>();
            data.put("mainPackage", Constants.GENERATED_CRSX_PLUGIN_PACKAGE);
            data.put("arguments", PGToXtext.getArguments());
            data.put("plugins", plugins);
        }
        
        @Override
        public Object getData() {
            return data;
        }
        
    }

    @Override
    public String getPluginPackage() {
        return null;
    }

    @Override
    public String getPluginName() {
        return null;
    }
    
}
