/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.crsx;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

import net.sf.crsx.pgutil.Arguments;
import net.sf.crsx.pgutil.Constants;
import net.sf.crsx.pgutil.PGToXtext;
import net.sf.crsx.pgutil.common.Plugin;
import net.sf.crsx.pgutil.common.Project;
import net.sf.crsx.pgutil.common.ResourceFileProvider;
import net.sf.crsx.pgutil.common.TargetDefinitionProject;
import net.sf.crsx.pgutil.common.TemplateFileProvider;
import net.sf.crsx.pgutil.common.Util;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;



public class CRSXPlugin extends Plugin{
    
    List<PGGrammar> embeddedGrammars;
    
    Project baseProject;
    Project sdkProject;
    Project uiProject;
    Project parentProject;
    Project updateSiteProject;
    Project targetProject;
    
    UniversalDataProvider dataProvider;
    AntlrLexerDataProvider antlrDataProvider;
    
    public CRSXPlugin(){
        embeddedGrammars = new LinkedList<PGGrammar>();
        
        String baseProjectName = Constants.GENERATED_CRSX_PLUGIN_PACKAGE;
        baseProject = new Project(baseProjectName);
        
        String sdkProjectName = String.format("%s.sdk", baseProjectName );
        sdkProject = new Project(sdkProjectName);
        
        String uiProjectName = String.format("%s.ui", baseProjectName );
        uiProject = new Project(uiProjectName);
        
        String parentProjectName = String.format("%s.parent", baseProjectName);
        parentProject = new Project(parentProjectName);
        
        String updateSiteProjectName = String.format("%s.updatesite", baseProjectName);
        updateSiteProject = new Project(updateSiteProjectName);
        
        Arguments arguments = PGToXtext.getArguments();
        
        if( arguments.isTargetDefinitionUse() ){
            String targetProjectName = String.format("%s.target", baseProjectName);
            targetProject = new TargetDefinitionProject(targetProjectName,baseProject,arguments);
        }
    }
    
    public void addEmbeddedGrammar(PGGrammar grammar){
        embeddedGrammars.add(grammar);
    }
    
    private void createStructureOfProjects(){
        
        dataProvider = new UniversalDataProvider(embeddedGrammars);
        antlrDataProvider = new AntlrLexerDataProvider(embeddedGrammars);
        
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

    private void createBaseProjectDirectoryStructure() {
        
        //This is the root project of main directory
        File projectRootDir = baseProject.getProjectDirectory();
        
        //Add eclipse .project file
        File projectFile = new File(projectRootDir,".project");
        TemplateFileProvider projectFileProvider = new TemplateFileProvider("crsx/base/project.ftl",
                dataProvider);
        baseProject.addFileCreateTask(projectFile, projectFileProvider);
        
        //Add eclipse .classpath file
        File classpathFile = new File(projectRootDir,".classpath");
        ResourceFileProvider classpathFileProvider = new ResourceFileProvider("static/crsx/base/classpath");
        baseProject.addFileCreateTask(classpathFile, classpathFileProvider);
        
        //Add eclipse build.properties file
        File buildPropertiesFile = new File(projectRootDir,"build.properties");
        ResourceFileProvider buildPropertiesFileProvider = new ResourceFileProvider("static/crsx/base/build.properties");
        baseProject.addFileCreateTask(buildPropertiesFile, buildPropertiesFileProvider);
        
        //Add eclipse .launch directory
        File launchDir = new File(projectRootDir,".launch");
        baseProject.addDirectory(launchDir);
        
        //Add eclipse .launch file
        String launchFilename = String.format("Generate Language Infrastructure (%s).launch", Constants.GENERATED_CRSX_PLUGIN_PACKAGE);
        File launchFile = new File(launchDir,launchFilename);
        TemplateFileProvider launchFileProvider = new TemplateFileProvider(
                "crsx/base/GenerateLanguageInfrastructure.launch.ftl", dataProvider);
        baseProject.addFileCreateTask(launchFile, launchFileProvider);
        
        //Add pom.xml
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("crsx/base/pom.xml.ftl",
                dataProvider);
        baseProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        //Add plugin.xml
        File pluginXMLFile = new File(projectRootDir,"plugin.xml");
        TemplateFileProvider pluginXMLFileProvider = new TemplateFileProvider("crsx/base/plugin.xml.ftl",
                dataProvider);
        baseProject.addFileCreateTask(pluginXMLFile, pluginXMLFileProvider);
        
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
        
        //Add META-INF directory
        File metaInfDir = new File(projectRootDir,"META-INF");
        baseProject.addDirectory(metaInfDir);
        
        File manifestFile = new File(metaInfDir,"MANIFEST.MF");
        TemplateFileProvider manifestFileProvider = new TemplateFileProvider(
                "crsx/base/MANIFEST.MF.ftl",dataProvider);
        baseProject.addFileCreateTask(manifestFile, manifestFileProvider);
        
        //Create directories coresponding to root java package of this project
        File basePackage = new File(srcDir,getDirectoriesFromQualifiedName(baseProject.getProjectName()).getPath());
        baseProject.addDirectory(basePackage);
        
        //MWE2 File
        File mwe2File = new File(basePackage,"GenerateCrsx.mwe2");
        TemplateFileProvider mwe2FileProvider = new TemplateFileProvider("crsx/base/GenerateCrsx.mwe2.ftl",
                dataProvider);
        baseProject.addFileCreateTask(mwe2File, mwe2FileProvider);
        
        File lexerPackage = new File(basePackage,"lexer");
        baseProject.addDirectory(lexerPackage);
        
        File lexerFile = new File(lexerPackage,"CrsxCustomLexer.g");
        TemplateFileProvider lexerFileProvider = new TemplateFileProvider("crsx/base/CrsxCustomLexer.g.ftl",
                antlrDataProvider);
        baseProject.addFileCreateTask(lexerFile, lexerFileProvider);
        
        File overridingLexerFile = new File(lexerPackage,"Lexer.java");
        TemplateFileProvider overridingLexerFileProvider = new TemplateFileProvider("crsx/base/Lexer.java.ftl",
                antlrDataProvider);
        baseProject.addFileCreateTask(overridingLexerFile, overridingLexerFileProvider);
        
        File xtextGrammarFile = new File(basePackage,"Crsx.xtext");
        TemplateFileProvider xtextGrammarFileProvider = new TemplateFileProvider("crsx/base/Crsx.xtext.ftl",
                antlrDataProvider);
        baseProject.addFileCreateTask(xtextGrammarFile, xtextGrammarFileProvider);
        
        File runtimeModuleFile = new File(basePackage,"CrsxRuntimeModule.java");
        TemplateFileProvider runtimeModuleFileProvider = new TemplateFileProvider("crsx/base/CrsxRuntimeModule.java.ftl",
                dataProvider);
        baseProject.addFileCreateTask(runtimeModuleFile, runtimeModuleFileProvider);
        
        File standaloneSetupFile = new File(basePackage,"CrsxStandaloneSetup.java");
        TemplateFileProvider standaloneSetupFileProvider = new TemplateFileProvider("crsx/base/CrsxStandaloneSetup.java.ftl",
                dataProvider);
        baseProject.addFileCreateTask(standaloneSetupFile, standaloneSetupFileProvider);
        
        File utilsFile = new File(basePackage,"Utils.java");
        TemplateFileProvider utilsFileProvider = new TemplateFileProvider("crsx/base/Utils.java.ftl",
                dataProvider);
        baseProject.addFileCreateTask(utilsFile, utilsFileProvider);
        
        File formattingPackage = new File(basePackage,"formatting");
        baseProject.addDirectory(formattingPackage);
        
        File formatterFile = new File(formattingPackage,"CrsxFormatter.java");
        TemplateFileProvider formatterFileProvider = new TemplateFileProvider("crsx/base/CrsxFormatter.java.ftl",
                dataProvider);
        baseProject.addFileCreateTask(formatterFile, formatterFileProvider);
        
        File generatorPackage = new File(basePackage,"generator");
        baseProject.addDirectory(generatorPackage);
        
        File generatorFile = new File(generatorPackage,"CrsxGenerator.xtend");
        TemplateFileProvider generatorFileProvider = new TemplateFileProvider("crsx/base/CrsxGenerator.xtend.ftl",
                dataProvider);
        baseProject.addFileCreateTask(generatorFile, generatorFileProvider);
        
        File scopingPackage = new File(basePackage,"scoping");
        baseProject.addDirectory(scopingPackage);
        
        File globalScopeProviderFile = new File(scopingPackage,"CrsxGlobalScopeProvider.java");
        TemplateFileProvider globalScopeProviderFileProvider = new TemplateFileProvider("crsx/base/CrsxGlobalScopeProvider.java.ftl",
                dataProvider);
        baseProject.addFileCreateTask(globalScopeProviderFile, globalScopeProviderFileProvider);
        
        File linkingDiagnosticMessageProviderFile = new File(scopingPackage,"CrsxLinkingDiagnosticMessageProvider.java");
        TemplateFileProvider linkingDiagnosticMessageProviderFileProvider = new TemplateFileProvider(
                "crsx/base/CrsxLinkingDiagnosticMessageProvider.java.ftl",dataProvider);
        baseProject.addFileCreateTask(linkingDiagnosticMessageProviderFile, linkingDiagnosticMessageProviderFileProvider);
        
        File qualifiedNameProviderFile = new File(scopingPackage,"CrsxQualifiedNameProvider.java");
        TemplateFileProvider qualifiedNameProviderFileProvider = new TemplateFileProvider(
                "crsx/base/CrsxQualifiedNameProvider.java.ftl",dataProvider);
        baseProject.addFileCreateTask(qualifiedNameProviderFile, qualifiedNameProviderFileProvider);
        
        File scopeProviderFile = new File(scopingPackage,"CrsxScopeProvider.java");
        TemplateFileProvider scopeProviderFileProvider = new TemplateFileProvider(
                "crsx/base/CrsxScopeProvider.java.ftl",dataProvider);
        baseProject.addFileCreateTask(scopeProviderFile, scopeProviderFileProvider);
        
        File validationPackage = new File(basePackage,"validation");
        baseProject.addDirectory(validationPackage);
        
        File javaValidatorFile = new File(validationPackage,"CrsxJavaValidator.java");
        TemplateFileProvider javaValidatorFileProvider = new TemplateFileProvider(
                "crsx/base/CrsxJavaValidator.java.ftl",dataProvider);
        baseProject.addFileCreateTask(javaValidatorFile, javaValidatorFileProvider);
    }

    private void createUpdateSiteProjectDirectoryStructure() {
        
        //This is the root of updatesite project directory
        File projectRootDir = updateSiteProject.getProjectDirectory();
        
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider(
                "crsx/updatesite/pom.xml.ftl",dataProvider);
        updateSiteProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        File categoryXMLFile = new File(projectRootDir,"category.xml");
        TemplateFileProvider categoryXMLFileProvider = new TemplateFileProvider(
                "crsx/updatesite/category.xml.ftl",dataProvider);
        updateSiteProject.addFileCreateTask(categoryXMLFile, categoryXMLFileProvider);
    }
    
    private void createUIProjectDirectoryStructure() {
        
        //This is the root of ui project directory
        File projectRootDir = uiProject.getProjectDirectory();
        
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider(
                "crsx/ui/pom.xml.ftl",dataProvider);
        uiProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        File buildPropertiesFile = new File(projectRootDir,"build.properties");
        ResourceFileProvider buildPropertiesFileProvider = new ResourceFileProvider(
                "static/crsx/ui/build.properties");
        uiProject.addFileCreateTask(buildPropertiesFile, buildPropertiesFileProvider);
        
        File pluginXMLFile = new File(projectRootDir,"plugin.xml");
        TemplateFileProvider pluginXMLFileProvider = new TemplateFileProvider(
                "crsx/ui/plugin.xml.ftl",dataProvider);
        uiProject.addFileCreateTask(pluginXMLFile, pluginXMLFileProvider);
        
        //Add eclipse project file
        File projectFile = new File(projectRootDir,".project");
        TemplateFileProvider projectFileProvider = new TemplateFileProvider(
                "crsx/ui/project.ftl",dataProvider);
        uiProject.addFileCreateTask(projectFile, projectFileProvider);
        
        //Add eclipse classpath file
        File classpathFile = new File(projectRootDir,".classpath");
        ResourceFileProvider classpathFileProvider = new ResourceFileProvider(
                "static/crsx/ui/classpath");
        uiProject.addFileCreateTask(classpathFile, classpathFileProvider);
        
        File srcDir = new File(projectRootDir,"src");
        uiProject.addDirectory(srcDir);
        
        //Create base java package
        String basePackagePath = getDirectoriesFromQualifiedName(uiProject.getProjectName()).getPath();
        File basePackageDir = new File(srcDir,basePackagePath);
        uiProject.addDirectory(basePackageDir);
        
        //Add CrsxUiModule.java
        File uiModuleFile = new File(basePackageDir,"CrsxUiModule.java");
        TemplateFileProvider uiModuleFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxUiModule.java.ftl",dataProvider);
        uiProject.addFileCreateTask(uiModuleFile, uiModuleFileProvider);
        
        //Add content assist java files
        File contentAssistPackageDir = new File(basePackageDir,"contentassist");
        uiProject.addDirectory(contentAssistPackageDir);
        
        File editStrategyProviderFile = new File(contentAssistPackageDir,"CrsxEditStrategyProvider.java");
        TemplateFileProvider editStrategyProviderFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxEditStrategyProvider.java.ftl",dataProvider);
        uiProject.addFileCreateTask(editStrategyProviderFile, editStrategyProviderFileProvider);
        
        File proposalProviderFile = new File(contentAssistPackageDir,"CrsxProposalProvider.java");
        TemplateFileProvider proposalProviderFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxProposalProvider.java.ftl",dataProvider);
        uiProject.addFileCreateTask(proposalProviderFile, proposalProviderFileProvider);
        
        //Add labeling java files
        File labelingPackageDir = new File(basePackageDir,"labeling");
        uiProject.addDirectory(labelingPackageDir);
        
        File descriptionLabelProviderFile = new File(labelingPackageDir,"CrsxDescriptionLabelProvider.java");
        TemplateFileProvider descriptionLabelProviderFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxDescriptionLabelProvider.java.ftl",antlrDataProvider);
        uiProject.addFileCreateTask(descriptionLabelProviderFile, descriptionLabelProviderFileProvider);
        
        File labelProviderFile = new File(labelingPackageDir,"CrsxLabelProvider.java");
        TemplateFileProvider labelProviderFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxLabelProvider.java.ftl",antlrDataProvider);
        uiProject.addFileCreateTask(labelProviderFile, labelProviderFileProvider);
        
        //Add outline java files
        File outlinePackageDir = new File(basePackageDir,"outline");
        uiProject.addDirectory(outlinePackageDir);
        
        File outlineTreeProviderFile = new File(outlinePackageDir,"CrsxOutlineTreeProvider.java");
        TemplateFileProvider outlineTreeProviderFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxOutlineTreeProvider.java.ftl",antlrDataProvider);
        uiProject.addFileCreateTask(outlineTreeProviderFile, outlineTreeProviderFileProvider);
        
        File groupingActionFile = new File(outlinePackageDir,"CrsxGroupFunctionsAction.java");
        TemplateFileProvider groupingActionFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxGroupFunctionsAction.java.ftl",dataProvider);
        uiProject.addFileCreateTask(groupingActionFile, groupingActionFileProvider);
        
        //Add preferences java files
        File preferencesPackageDir = new File(basePackageDir,"preferences");
        uiProject.addDirectory(preferencesPackageDir);
        
        File rootPreferencePageFile = new File(preferencesPackageDir,"CrsxRootPreferencePage.java");
        TemplateFileProvider rootPreferencePageFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxRootPreferencePage.java.ftl",dataProvider);
        uiProject.addFileCreateTask(rootPreferencePageFile, rootPreferencePageFileProvider);
        
        //Add quickfix java files
        File quickfixPackageDir = new File(basePackageDir,"quickfix");
        uiProject.addDirectory(quickfixPackageDir);
        
        File quickfixProviderFile = new File(quickfixPackageDir,"CrsxQuickfixProvider.java");
        TemplateFileProvider quickfixProviderFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxQuickfixProvider.java.ftl",dataProvider);
        uiProject.addFileCreateTask(quickfixProviderFile, quickfixProviderFileProvider);
        
        //Add syntax coloring java files
        File syntaxcoloringPackageDir = new File(basePackageDir,"syntaxcoloring");
        uiProject.addDirectory(syntaxcoloringPackageDir);
        
        File highlightingConfigurationFile = new File(syntaxcoloringPackageDir,"CrsxHighlightingConfiguration.java");
        TemplateFileProvider highlightingConfigurationFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxHighlightingConfiguration.java.ftl",antlrDataProvider);
        uiProject.addFileCreateTask(highlightingConfigurationFile, highlightingConfigurationFileProvider);
        
        File semanticHighlightingCalculatorFile = new File(syntaxcoloringPackageDir,"CrsxSemanticHighlightingCalculator.java");
        TemplateFileProvider semanticHighlightingCalculatorFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxSemanticHighlightingCalculator.java.ftl",antlrDataProvider);
        uiProject.addFileCreateTask(semanticHighlightingCalculatorFile, semanticHighlightingCalculatorFileProvider);
        
        File tokenToAttributeIdMapperFile = new File(syntaxcoloringPackageDir,"CrsxTokenToAttributeIdMapper.java");
        TemplateFileProvider tokenToAttributeIdMapperFileProvider = new TemplateFileProvider(
                "crsx/ui/CrsxTokenToAttributeIdMapper.java.ftl",antlrDataProvider);
        uiProject.addFileCreateTask(tokenToAttributeIdMapperFile, tokenToAttributeIdMapperFileProvider);
        
        File srcGenDir = new File(projectRootDir,"src-gen");
        uiProject.addDirectory(srcGenDir);
        
        File metaInfDir = new File(projectRootDir,"META-INF");
        uiProject.addDirectory(metaInfDir);
        
        File manifestFile = new File(metaInfDir,"MANIFEST.MF");
        TemplateFileProvider manifestFileProvider = new TemplateFileProvider(
                "crsx/ui/MANIFEST.MF.ftl",dataProvider);
        uiProject.addFileCreateTask(manifestFile, manifestFileProvider);
        
    }

    private void createSDKProjectDirectoryStructure() {
        
        //This is the root project of sdk project directory
        File projectRootDir = sdkProject.getProjectDirectory();
        
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider(
                "crsx/sdk/pom.xml.ftl",dataProvider);
        sdkProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
        //Add build.properties file
        File buildPropertiesFile = new File(projectRootDir,"build.properties");
        ResourceFileProvider buildPropertiesFileProvider = new ResourceFileProvider("static/crsx/sdk/build.properties");
        sdkProject.addFileCreateTask(buildPropertiesFile, buildPropertiesFileProvider);
        
        //Add feature.xml file
        File featureXMLFile = new File(projectRootDir,"feature.xml");
        TemplateFileProvider featureXMLFileProvider = new TemplateFileProvider(
                "crsx/sdk/feature.xml.ftl",dataProvider);
        sdkProject.addFileCreateTask(featureXMLFile, featureXMLFileProvider);
        
        //Add eclipse .project file
        File projectFile = new File(projectRootDir,".project");
        TemplateFileProvider projectFileProvider = new TemplateFileProvider(
                "crsx/sdk/project.ftl",dataProvider);
        sdkProject.addFileCreateTask(projectFile, projectFileProvider);
        
    }

    private void createParentProjectDirectoryStructure() {
        
        //This is the root project of parent project directory
        File projectRootDir = parentProject.getProjectDirectory();
        
        //Add pom.xml file
        File pomXMLFile = new File(projectRootDir,"pom.xml");
        TemplateFileProvider pomXMLFileProvider = new TemplateFileProvider("crsx/parent/pom.xml.ftl",
                dataProvider);
        parentProject.addFileCreateTask(pomXMLFile, pomXMLFileProvider);
        
    }
    
    class UniversalDataProvider implements TemplateFileProvider.TemplateDataProvider {

        Map<String,Object> data;
        
        public UniversalDataProvider(List<PGGrammar> grammars){
            data = new TreeMap<String,Object>();
            
            String mainPackage = Constants.GENERATED_CRSX_PLUGIN_PACKAGE;
            
            data.put("package", Constants.GENERATED_CRSX_PLUGIN_PACKAGE);
            data.put("vendor", PGToXtext.getArguments().getVendor());
            data.put("version", PGToXtext.getArguments().getVersion());
            
            File basePackageDirFile = getDirectoriesFromQualifiedName(mainPackage);
            
            //Xtext grammar path
            String basePackagePath = String.format("src/%s",basePackageDirFile.getPath());
            File baseSrcPackageFile = new File(basePackagePath);
            File xtextGrammarFile = new File(baseSrcPackageFile,"Crsx.xtext");
            
            data.put("xtextPath", xtextGrammarFile.getPath());
            
            //MWE2 path
            
            File mwe2File = new File(baseSrcPackageFile,"GenerateCrsx.mwe2");
            
            data.put("mwe2Path", mwe2File.getPath());
            
            //Generated lexer path
            String lexerGenPackagePath = String.format("src-gen/%s/lexer", basePackageDirFile.getPath());
            File lexerGenDirFile = new File(lexerGenPackagePath);
            
            data.put("lexerPath", lexerGenDirFile.getPath());
            
            //URI of the package
            String URI = Util.generateNamespaceURIForQualifiedName(mainPackage);
            data.put("uri",URI);
            
            data.put("basePackagePath",basePackageDirFile.getPath());
            data.put("arguments", PGToXtext.getArguments());
        }
        
        @Override
        public Object getData() {
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
        return baseProject.getProjectName();
    }

    @Override
    public String getPluginName() {
        if(embeddedGrammars.isEmpty()){
            return "CRSX plugin";
        }else{
            String result  = "CRSX plugin with support for";
            for(PGGrammar grammar : embeddedGrammars){
                result += " " + grammar.getName();
            }
            return result;
        }
    }
}
