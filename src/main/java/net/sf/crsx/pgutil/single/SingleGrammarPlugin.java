/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.single;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;

import net.sf.crsx.pgutil.Arguments;
import net.sf.crsx.pgutil.PGToXtext;
import net.sf.crsx.pgutil.common.Plugin;
import net.sf.crsx.pgutil.common.Project;
import net.sf.crsx.pgutil.common.ResourceFileProvider;
import net.sf.crsx.pgutil.common.TargetDefinitionProject;
import net.sf.crsx.pgutil.common.TemplateFileProvider;
import net.sf.crsx.pgutil.common.Util;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGOutlineHints.PGOutlineHint;

public class SingleGrammarPlugin extends Plugin{
    
    static final Logger LOGGER = Logger.getGlobal();
    
    PGGrammar grammar;
    List<Project> projects;
    
    Project baseProject;
    Project sdkProject;
    Project uiProject;
    Project parentProject;
    Project updateSiteProject;
    Project targetProject;
    
    String baseProjectName;
    
    TemplateFileProvider.TemplateDataProvider uniDataProvider;
    
    public SingleGrammarPlugin(PGGrammar grammar){
        this.grammar = grammar;
        projects = new ArrayList<Project>();
        
        baseProjectName = grammar.getGrammarPackage();
        baseProject = new Project(baseProjectName);
        
        String sdkProjectName = String.format("%s.sdk", baseProjectName);
        sdkProject = new Project(sdkProjectName);
        
        String uiProjectName = String.format("%s.ui", baseProjectName);
        uiProject = new Project(uiProjectName);
        
        String parentProjectName = String.format("%s.parent", baseProjectName);
        parentProject = new Project(parentProjectName);
        
        String updateSiteProjectName = String.format("%s.updatesite",baseProjectName);
        updateSiteProject = new Project(updateSiteProjectName);
        
        uniDataProvider = new UniversalDataProvider(grammar);
        
        Arguments arguments = PGToXtext.getArguments();
        
        if( arguments.isTargetDefinitionUse() ){
            String targetProjectName = String.format("%s.target", baseProjectName);
            targetProject = new TargetDefinitionProject(targetProjectName,baseProject,arguments);
        }
    }
    
    private void createStructureOfProjects(){
        createBaseProjectDirectoryStructure();
        createSDKProjectDirectoryStructure();
        createUIProjectDirectoryStructure();
        createParentProjectDirectoryStructure();
        createUpdateSiteProjectDirectoryStructure();
    }
    
    /**
     * Generate directory structure and files for all projects
     * 
     * @param rootDirectory Directory where resulting projects will be created
     */
    public void generatePluginSources(File rootDirectory){
        createStructureOfProjects();
        
        baseProject.createDirectories(rootDirectory);
        baseProject.writeFiles(rootDirectory);
        
        sdkProject.createDirectories(rootDirectory);
        sdkProject.writeFiles(rootDirectory);
        
        uiProject.createDirectories(rootDirectory);
        uiProject.writeFiles(rootDirectory);
        
        parentProject.createDirectories(rootDirectory);
        parentProject.writeFiles(rootDirectory);
        
        updateSiteProject.createDirectories(rootDirectory);
        updateSiteProject.writeFiles(rootDirectory);
        
        if(targetProject != null){
            targetProject.createDirectories(rootDirectory);
            targetProject.writeFiles(rootDirectory);
        }

    }
    
    /**
     * Generate directory structure and files for all projects
     * 
     * @param zip ZipOutputStream to which files will be packed
     */
    public void generatePluginSources(ZipOutputStream zip){
        createStructureOfProjects();
        
        baseProject.createDirectories(zip);
        baseProject.writeFiles(zip);
        
        sdkProject.createDirectories(zip);
        sdkProject.writeFiles(zip);
        
        uiProject.createDirectories(zip);
        uiProject.writeFiles(zip);
        
        parentProject.createDirectories(zip);
        parentProject.writeFiles(zip);
        
        updateSiteProject.createDirectories(zip);
        updateSiteProject.writeFiles(zip);
        
        if(targetProject != null){
            targetProject.createDirectories(zip);
            targetProject.writeFiles(zip);
        }

    }
    
    /**
     * Create directory and file structure for main project
     */
    public void createBaseProjectDirectoryStructure(){
        //This is the root project of main directory
        File projectRootDir = baseProject.getProjectDirectory();
        String grammarNameUpper =  grammar.getName().toUpperCase();
        
        //Add src directory
        File srcDir = new File(projectRootDir,"src");
        baseProject.addDirectory(srcDir);
        
        //Add src-gen directory
        File srcGenDir = new File(projectRootDir,"src-gen");
        baseProject.addDirectory(srcGenDir);
        
        //Add xtend-gen directory
        File xtendGenDir = new File(projectRootDir,"xtend-gen");
        baseProject.addDirectory(xtendGenDir);
        
        //Add model directory
        File modelDir = new File(projectRootDir,"model");
        baseProject.addDirectory(modelDir);
        
        //Create directories coresponding to root java package of this project
        File basePackage = new File(srcDir,getDirectoriesFromQualifiedName(baseProject.getProjectName()).getPath());
        baseProject.addDirectory(basePackage);
        
        //Add xtext file
        File xtextFile = new File(basePackage,String.format("%s.xtext",grammarNameUpper));
        SingleXtextGrammarFileProvider xtextProvider = new SingleXtextGrammarFileProvider(grammar);
        baseProject.addFileCreateTask(xtextFile, xtextProvider);
        
        //Add MANIFEST.mf file
        File metaInfDir = new File(projectRootDir,"META-INF");
        baseProject.addDirectory( metaInfDir );
        File manifestFile = new File(metaInfDir, "MANIFEST.MF");
        TemplateFileProvider manifestFileProvider = new TemplateFileProvider("single/base/MANIFEST.MF.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(manifestFile, manifestFileProvider);
        
        //Add build.properties
        File buildProperties = new File(projectRootDir,"build.properties");
        ResourceFileProvider buildPropertiesProvider = new ResourceFileProvider("static/single/base/build.properties");
        baseProject.addFileCreateTask(buildProperties, buildPropertiesProvider);
        
        //Add plugin.xml
        File pluginXMLFile = new File(projectRootDir,"plugin.xml");
        TemplateFileProvider pluginXMLFileProvider = new TemplateFileProvider("single/base/plugin.xml.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(pluginXMLFile, pluginXMLFileProvider);
        
        //Add Generate<GRAMMAR>.mwe2
        String mweFilename = String.format("Generate%s.mwe2", grammarNameUpper);
        File mweFile = new File(basePackage,mweFilename);
        TemplateFileProvider mwe2FileProvider = new TemplateFileProvider("single/base/Generate.mwe.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(mweFile, mwe2FileProvider);
        
        //Add <GRAMMAR>RuntimeModule.java
        String runtimeModuleFilename = String.format("%sRuntimeModule.java", grammarNameUpper);
        File runtimeModuleFile = new File(basePackage,runtimeModuleFilename);
        TemplateFileProvider runtimeModuleFileProvider = new TemplateFileProvider("single/base/RuntimeModule.java.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(runtimeModuleFile, runtimeModuleFileProvider);
        
        //Add <GRAMMAR>StandaloneSetup.java
        String standaloneSetupFilename = String.format("%sStandaloneSetup.java",grammarNameUpper);
        File standaloneSetupFile = new File(basePackage, standaloneSetupFilename);
        TemplateFileProvider standaloneSetupFileProvider = new TemplateFileProvider("single/base/StandaloneSetup.java.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(standaloneSetupFile, standaloneSetupFileProvider);
        
        //Add <GRAMMAR>Formatter.java
        File formattingPackage = new File(basePackage,"formatting");
        baseProject.addDirectory(formattingPackage);
        String formatterFilename = String.format("%sFormatter.java",grammarNameUpper);
        File formatterFile = new File(formattingPackage,formatterFilename);
        TemplateFileProvider formatterFileProvider = new TemplateFileProvider("single/base/Formatter.java.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(formatterFile, formatterFileProvider);
        
        //Add <GRAMMAR>Generator.xtend
        File generatorPackage = new File(basePackage,"generator");
        baseProject.addDirectory(generatorPackage);
        String generatorFilename = String.format("%sGenerator.xtend",grammarNameUpper);
        File generatorFile = new File(generatorPackage,generatorFilename);
        TemplateFileProvider generatorFileProvider = new TemplateFileProvider("single/base/Generator.xtend.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(generatorFile, generatorFileProvider);
        
        //Add <GRAMMAR>ScopeProvider.java
        File scopingPackage = new File(basePackage,"scoping");
        baseProject.addDirectory(scopingPackage);
        String scopeProviderFilename = String.format("%sScopeProvider.java",grammarNameUpper);
        File scopeProviderFile = new File(scopingPackage,scopeProviderFilename);
        TemplateFileProvider scopeProviderFileProvider = new TemplateFileProvider("single/base/ScopeProvider.java.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(scopeProviderFile, scopeProviderFileProvider);
        
        //Add <GRAMMAR>JavaValidator.java
        File validationPackage = new File(basePackage,"validation");
        baseProject.addDirectory(validationPackage);
        String validatorFilename = String.format("%sJavaValidator.java", grammarNameUpper);
        File validatorFile = new File(validationPackage,validatorFilename);
        TemplateFileProvider validatorFileProvider = new TemplateFileProvider("single/base/JavaValidator.java.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(validatorFile, validatorFileProvider);
        
        //Add Eclipse .project file
        File eclipseProjectFile = new File(projectRootDir,".project");
        TemplateFileProvider eclipseProjectFileProvider = new TemplateFileProvider("single/base/project.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(eclipseProjectFile, eclipseProjectFileProvider);
        
        //Add Eclipse .classpath file
        File eclipseClasspathFile = new File(projectRootDir,".classpath");
        ResourceFileProvider eclipseClasspathFileProvider = new ResourceFileProvider("static/single/base/classpath");
        baseProject.addFileCreateTask(eclipseClasspathFile, eclipseClasspathFileProvider);
        
        //Add Eclipse .launch directory
        File launchDir = new File(projectRootDir,".launch");
        baseProject.addDirectory(launchDir);
        
        //Add Generate.launch file
        String launchFilename = String.format("Generate Language Infrastructure (%s).launch",baseProject.getProjectName());
        File launchFile = new File(launchDir,launchFilename);
        TemplateFileProvider eclipseLaunchFileProvider = new TemplateFileProvider("single/base/LaunchConfiguration.launch.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(launchFile, eclipseLaunchFileProvider);
        
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("single/base/pom.xml.ftl",
                uniDataProvider);
        baseProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
    }
    
    /**
     * Create directory and file structure for SDK project
     */
    public void createSDKProjectDirectoryStructure(){
        File projectRootDir = sdkProject.getProjectDirectory();
        
        File buildPropertiesFile = new File(projectRootDir,"build.properties");
        ResourceFileProvider buildPropertiesProvider = new ResourceFileProvider("static/single/sdk/build.properties");
        sdkProject.addFileCreateTask(buildPropertiesFile, buildPropertiesProvider);
        
        File featureXMLFile = new File(projectRootDir,"feature.xml");
        TemplateFileProvider featureXMLFileProvider = new TemplateFileProvider("single/sdk/feature.xml.ftl",
                uniDataProvider);
        sdkProject.addFileCreateTask(featureXMLFile, featureXMLFileProvider);
        
        //Add Eclipse .project file
        File eclipseProjectFile = new File(projectRootDir,".project");
        TemplateFileProvider eclipseProjectFileProvider = new TemplateFileProvider("single/sdk/project.ftl",
                uniDataProvider);
        sdkProject.addFileCreateTask(eclipseProjectFile, eclipseProjectFileProvider);
        
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("single/sdk/pom.xml.ftl",
                uniDataProvider);
        sdkProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
    }
    
    /**
     * Create directory and file structure for UI project
     */
    public void createUIProjectDirectoryStructure(){
        File projectRootDir = uiProject.getProjectDirectory();
        String grammarNameUpper =  grammar.getName().toUpperCase();
        
        //Add build.properties
        File buildProperties = new File(projectRootDir,"build.properties");
        ResourceFileProvider buildPropertiesProvider = new ResourceFileProvider("static/single/ui/build.properties");
        uiProject.addFileCreateTask(buildProperties, buildPropertiesProvider);
        
        //Add plugin.xml
        File pluginXMLFile = new File(projectRootDir,"plugin.xml");
        TemplateFileProvider pluginXMLFileProvider = new TemplateFileProvider("single/ui/plugin.xml.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(pluginXMLFile,pluginXMLFileProvider);
        
        
        //Add Manifest.mf
        File metaInfDir = new File(projectRootDir,"META-INF");
        uiProject.addDirectory(metaInfDir);
        
        File manifestFile = new File(metaInfDir,"MANIFEST.MF");
        TemplateFileProvider manifestFileProvider = new TemplateFileProvider("single/ui/MANIFEST.MF.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(manifestFile, manifestFileProvider);
        
        File srcDir = new File(projectRootDir,"src");
        File srcGenDir = new File(projectRootDir,"src-gen");
        File xtendGenDir = new File(projectRootDir,"xtend-gen");
        uiProject.addDirectory(srcDir);
        uiProject.addDirectory(srcGenDir);
        uiProject.addDirectory(xtendGenDir);
        
        String basePackagePath = getDirectoriesFromQualifiedName(uiProject.getProjectName()).getPath();
        File basePackage = new File(srcDir,basePackagePath);
        
        uiProject.addDirectory(basePackage);
        
        //Add <GRAMMAR>UiModule.java
        String uiModuleFilename = String.format("%sUiModule.java",grammarNameUpper);
        File uiModuleFile = new File(basePackage,uiModuleFilename);
        TemplateFileProvider uiModuleFileProvider = new TemplateFileProvider("single/ui/UiModule.java.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(uiModuleFile, uiModuleFileProvider);
        
        //Add <GRAMMAR>ProposalProvider.xtend
        /*File contentAssistDir = new File(basePackage,"contentassist");
        uiProject.addDirectory(contentAssistDir);
        
        String proposalProviderFilename = String.format("%sProposalProvider.java",grammarNameUpper);
        File proposalProviderFile = new File(contentAssistDir,proposalProviderFilename);
        TemplateFileProvider proposalProviderFileProvider = new TemplateFileProvider("single/ui/ProposalProvider.java.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(proposalProviderFile, proposalProviderFileProvider);*/
        
        //Add <GRAMMAR>DescriptionLabelProvider.java
        File labelingDir = new File(basePackage,"labeling");
        uiProject.addDirectory(labelingDir);
        
        String descriptionLabelProviderFilename = String.format("%sDescriptionLabelProvider.java",grammarNameUpper);
        File descriptionLabelProviderFile = new File(labelingDir,descriptionLabelProviderFilename);
        TemplateFileProvider descriptionLabelProviderFileProvider = new TemplateFileProvider("single/ui/DescriptionLabelProvider.java.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(descriptionLabelProviderFile, descriptionLabelProviderFileProvider);
        
        //Add <GRAMMAR>LabelProvider.java
        String labelProviderFilename = String.format("%sLabelProvider.java",grammarNameUpper);
        File labelProviderFile = new File(labelingDir,labelProviderFilename);
        TemplateFileProvider labelProviderFileProvider = new TemplateFileProvider("single/ui/LabelProvider.java.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(labelProviderFile, labelProviderFileProvider);
        
        //Add <GRAMMAR>OutlineTreeProvider.java
        File outlineDir = new File(basePackage,"outline");
        uiProject.addDirectory(outlineDir);
        
        String outlineTreeProviderFilename = String.format("%sOutlineTreeProvider.java",grammarNameUpper);
        File outlineTreeProviderFile = new File(outlineDir,outlineTreeProviderFilename);
        TemplateFileProvider outlineTreeProviderFileProvider = new TemplateFileProvider("single/ui/OutlineTreeProvider.java.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(outlineTreeProviderFile, outlineTreeProviderFileProvider);
        
        //Add <GRAMMAR>QuickfixProvider.java
        File quickfixDir = new File(basePackage,"quickfix");
        uiProject.addDirectory(quickfixDir);
        
        String quickfixProviderFilename = String.format("%sQuickfixProvider.java",grammarNameUpper);
        File quickfixProviderFile = new File(quickfixDir,quickfixProviderFilename);
        TemplateFileProvider quickfixProviderFileProvider = new TemplateFileProvider("single/ui/QuickfixProvider.java.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(quickfixProviderFile, quickfixProviderFileProvider);
        
        //Add Eclipse .project file
        File eclipseProjectFile = new File(projectRootDir,".project");
        TemplateFileProvider eclipseProjectFileProvider = new TemplateFileProvider("single/ui/project.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(eclipseProjectFile, eclipseProjectFileProvider);
        
        //Add Eclipse .classpath file
        File eclipseClasspathFile = new File(projectRootDir,".classpath");
        ResourceFileProvider eclipseClasspathFileProvider = new ResourceFileProvider("static/single/ui/classpath");
        uiProject.addFileCreateTask(eclipseClasspathFile, eclipseClasspathFileProvider);
      
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("single/ui/pom.xml.ftl",
                uniDataProvider);
        uiProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
    }
    
    /**
     * Create directory and file structure for parent project
     * 
     * Parent project is just a helper for maven build, it contains only parent pom.xml.
     * Building this project will result to building all the child projects and creating 
     * update site repository in target/repository directory of updatesite project.
     */
    public void createParentProjectDirectoryStructure(){
        File projectRootDir = parentProject.getProjectDirectory();
        
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("single/parent/pom.xml.ftl",
                uniDataProvider);
        parentProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
    }
    
    /**
     * Create directory and file structure for updatesite project
     */
    public void createUpdateSiteProjectDirectoryStructure(){
        File projectRootDir = updateSiteProject.getProjectDirectory();
        
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("single/updatesite/pom.xml.ftl",
                uniDataProvider);
        updateSiteProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        //Add category.xml file
        File categoryXMLFile = new File(projectRootDir,"category.xml");
        TemplateFileProvider categoryXMLFileProvider = new TemplateFileProvider("single/updatesite/category.xml.ftl",
                uniDataProvider);
        updateSiteProject.addFileCreateTask(categoryXMLFile, categoryXMLFileProvider);
    }
    
    static class UniversalDataProvider implements TemplateFileProvider.TemplateDataProvider {
        Map<String,Object> data;
        PGGrammar grammar;
        
        public UniversalDataProvider(PGGrammar grammar){
            this.grammar = grammar;
            String grammarPackage = grammar.getGrammarPackage();
            String grammarNameUpper = grammar.getName().toUpperCase();
            String version = PGToXtext.getArguments().getVersion();
            String vendor = PGToXtext.getArguments().getVendor();
            String extensions = grammar.getExtension();
            String module = String.format("%s.Generate%s",grammarPackage,grammarNameUpper);
            String grammarURI = String.format("%s/%s.xtext",
                    getDirectoriesFromQualifiedName(grammarPackage).getPath(),
                    grammarNameUpper);
            String uri = Util.generateNamespaceURIForQualifiedName(grammar.getGrammarClassName());
            String grammarClassName = grammar.getGrammarClassName();
            String mweFilename = String.format("Generate%s.mwe2", grammarNameUpper);
            String mwe2path = getDirectoriesFromQualifiedName(grammarPackage).getPath() + "/" + mweFilename;
            Collection<PGOutlineHint> outlineHints = grammar.getOutlineHints().getOutlineHints();

            
            data = new TreeMap<String,Object>();
            data.put("package", grammarPackage);
            data.put("name", grammarNameUpper);
            data.put("version", version);
            data.put("vendor", vendor);
            data.put("extensions", extensions);
            data.put("projectName", grammarPackage);
            data.put("module", module);
            data.put("grammarURI", grammarURI);
            data.put("uri",uri);
            data.put("className",grammarClassName);
            data.put("mwe2path", mwe2path);
            data.put("outlineElements", outlineHints);
            data.put("arguments", PGToXtext.getArguments());
            
        }
        
        @Override
        public Object getData() {
            Collection<PGOutlineHint> outlineHints = grammar.getOutlineHints().getOutlineHints();
            data.put("outlineElements", outlineHints);
            return data;
        }
        
        
    }

    @Override
    public File getParentPOMFile() {
        return new File(parentProject.getProjectDirectory(),"pom.xml");
    }

    @Override
    public File getEclipseRepoPath() {
        return new File(updateSiteProject.getProjectDirectory(),"target/repository");
    }

    @Override
    public String getPluginPackage() {
        return grammar.getGrammarPackage();
    }

    @Override
    public String getPluginName() {
        return String.format("%s language", grammar.getName());
    }
}
