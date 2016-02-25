/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Arguments {
    
    private static Logger LOGGER = Logger.getGlobal();
    
    static final String GRAMMAR_ARGUMENT_WITHOUT_GRAMMAR_ERROR = 
            "Related input file must be specified before -s/--start argument";
    
    public static enum RunMode {
        //Show help and exit
        SHOW_HELP,
        //take single .pg file and produce single .xtext grammar
        CONVERT_SINGLE_JUST_GRAMMAR,
        //take single .pg file and produce single plugin supporting 
        //language corresponding to .pg file
        CONVERT_SINGLE,
        //take single or multiple .pg files and produce CRSX plugin
        //with languages corresponding .pg embedded into CRSX
        CRSX_EMBEDDED,
        //create plugin for each of the input .pg files
        //and also create CRSX plugin with support for embedded 
        //languages from .pg files
        CREATE_ALL
    }
    
    public static enum BuildMode {
        //do not build generated sources
        NO_BUILD,
        //generate sources to output directory and build them
        BUILD_GENERATED_SOURCES,
        //generate sources to output directory, build them and
        //copy updatesite to output location
        BUILD_UPDATESITE_ONLY
    }
    
    
    /**
     * Holds arguments related to specific input pg file
     */
    public static final class InputFileArgumentGroup {
        //.pg filename
        String filename;
        //starting (root) nonterminal of the grammar
        String rootNonterminal;
        //extension for sources in language specified by .pg
        String extension;
        
        public InputFileArgumentGroup(String filename){
            this.filename = filename;
        }
        
        public String getFilename(){
            return filename;
        }
        
        public void setRootNonTerminal(String rootNonterminal){
            this.rootNonterminal = rootNonterminal;
        }
        
        public String getRootNonTerminal(){
            return rootNonterminal;
        }
        
        public void setExtension(String extension){
            this.extension = extension;
        }
        
        public boolean isExtensionSet(){
            return extension == null;
        }
        
        public String getExtension(){
            return extension;
        }
    }

    List<Arguments.InputFileArgumentGroup> inputFiles;
    private String outputFilename;
    private String vendor;
    private String version;
    private String mavenPath;
    private String xtextVersion;
    private String xtextUpdateSite;
    private String customTargetDefinitionFile = null;
    private Arguments.RunMode runMode = RunMode.CONVERT_SINGLE;
    Arguments.BuildMode buildMode= BuildMode.NO_BUILD;
    boolean isOK = true;
    boolean zip = false;
    boolean snapshot = true;
    boolean targetDefinitionUse = true;
    boolean includeAllDependencies = false;
    String errorMsg = null;
    
    private Arguments(){
        vendor = Constants.GENERATED_PLUGIN_DEFAULT_VENDOR;
        version = Constants.GENERATED_PLUGIN_DEFAULT_VERSION;
        xtextVersion = Constants.DEFAULT_XTEXT_VERSION;
        xtextUpdateSite = Constants.DEFAULT_XTEXT_UPDATE_SITE;
        inputFiles = new ArrayList<Arguments.InputFileArgumentGroup>();
    }
    
    private static String retrieveArgument(int idx, String [] args, Arguments arguments, String defaultArg){
        int argIdx = idx + 1;
        if( argIdx < args.length ) {
            return args[argIdx];
        }else{
            arguments.isOK = false;
            return defaultArg;
        }
    }
    
    private void fail(String errorMsg){
        this.errorMsg = errorMsg;
        this.isOK = false;
    }
    
    private static boolean matchOption(String shortOpt,String longOpt,String arg){
        return shortOpt.equalsIgnoreCase(arg) || longOpt.equalsIgnoreCase(arg);
    }
    
    /**
     * Parse single command line option starting at args[idx], modify 
     * arguments according to the option and return idx of the first next
     * unparsed argument
     * 
     * @param idx index of option in args array
     * @param args array of arguments
     * @param arguments Arguments object to store option setting in
     * @return idx from which argument parsing should continue
     */
    private static int parseOption( int idx, String [] args, Arguments arguments ){
        String arg = args[idx].substring(1, args[idx].length());
        int next = idx+1;
        if( matchOption("o","-output",arg) ){
            arguments.outputFilename = retrieveArgument(idx,args,arguments,arguments.outputFilename);
            ++next;
        }else if ( matchOption("h","-help",arg) ){
            arguments.runMode = RunMode.SHOW_HELP;
        }else if ( matchOption("g","-single",arg) ){
            arguments.runMode = RunMode.CONVERT_SINGLE;
        }else if(matchOption("x","-xtext",arg)){
            arguments.runMode = RunMode.CONVERT_SINGLE_JUST_GRAMMAR;
        }else if(matchOption("c","-crsx",arg)){
            arguments.runMode = RunMode.CRSX_EMBEDDED;
        }else if(matchOption("a","-all",arg)){
            arguments.runMode = RunMode.CREATE_ALL;
        }else if(matchOption("b","-build",arg)){
            arguments.buildMode = BuildMode.BUILD_GENERATED_SOURCES;
        }else if(matchOption("u","-updatesite",arg)){
            arguments.buildMode = BuildMode.BUILD_UPDATESITE_ONLY;
        }else if(matchOption("z","-zip",arg)){
            arguments.zip = true;
        }else if (matchOption("sn","-snapshot",arg)){
            arguments.snapshot = true;
        }else if (matchOption("ns","-nosnapshot",arg)){
            arguments.snapshot = false;
        }else if (matchOption("r","-organization",arg) ){
            arguments.vendor = retrieveArgument(idx,args,arguments,Constants.GENERATED_PLUGIN_DEFAULT_VENDOR);
            ++next;
        }else if ( matchOption("v","-version",arg) ){
            arguments.setVersion(retrieveArgument(idx,args,arguments,Constants.GENERATED_PLUGIN_DEFAULT_VERSION));
            ++next;
        }else if ( matchOption("xu","-xtextupdatesite",arg)){
            arguments.xtextUpdateSite = retrieveArgument(idx,args,arguments,Constants.DEFAULT_XTEXT_UPDATE_SITE);
            ++next;
        }else if ( matchOption("xv","-xtextversion",arg)){
            arguments.xtextVersion = retrieveArgument(idx,args,arguments,Constants.DEFAULT_XTEXT_VERSION);
            ++next;
        }else if ( matchOption("t","-target",arg)){
            arguments.targetDefinitionUse = true;
        }else if ( matchOption("nt","-notarget",arg)){
            arguments.targetDefinitionUse = false;
        }else if ( matchOption("d","-include_dependencies",arg)){
            arguments.includeAllDependencies = true;
        }else if ( matchOption("nd","-no_include_dependencies",arg)){
            arguments.includeAllDependencies = false;
        }else if ( matchOption("tf","-targetfile",arg)){
            arguments.customTargetDefinitionFile = retrieveArgument(idx, args, arguments, null);
            arguments.targetDefinitionUse = true;
            ++next;
        }else if ( matchOption("s","-start",arg) ){
            if(arguments.inputFiles.isEmpty()){
                arguments.fail(GRAMMAR_ARGUMENT_WITHOUT_GRAMMAR_ERROR);
                return -1;
            }
            
            String rootNonterminal = retrieveArgument(idx,args,arguments,null);
            int lastIndex = arguments.inputFiles.size() - 1;
            arguments.inputFiles.get( lastIndex ).setRootNonTerminal(rootNonterminal);
            ++next;
        }else if ( matchOption("e","-extension",arg)){
            if(arguments.inputFiles.isEmpty()){
                arguments.fail(GRAMMAR_ARGUMENT_WITHOUT_GRAMMAR_ERROR);
                return -1;
            }
            String extension = retrieveArgument(idx,args,arguments,null);
            int lastIndex = arguments.inputFiles.size() - 1;
            arguments.inputFiles.get( lastIndex ).setExtension(extension);
            ++next;
        }else if ( matchOption("m","-maven",arg)){
            arguments.mavenPath = retrieveArgument(idx,args,arguments,null);
            ++next;
        }else{
            String errorMessage = String.format("Unknown argument %s", arg);
            LOGGER.log(Level.WARNING, errorMessage);
        }
        return next;
    }
    
    /**
     * Construct Arguments object from command line arguments
     * and validate them
     * 
     * @param args command line arguments array
     * @return Arguments object
     */
    public static Arguments parseArgs( String [] args ){
        Arguments result = new Arguments();
        boolean inputFilenameSet = false;
        int i = 0;
        while( i < args.length ){
            if( args[i].startsWith("-") ){
                i = parseOption(i,args,result);
                if(i < 0){
                    break;
                }
            }else{
                Arguments.InputFileArgumentGroup argGroup = 
                        new Arguments.InputFileArgumentGroup(args[i]);
                result.inputFiles.add( argGroup );
                inputFilenameSet = true;
                ++i;
            }
        }
        if(!inputFilenameSet){
            result.isOK = false;
            result.errorMsg = "No input grammar file was set.";
        }
        if(result.runMode != RunMode.CRSX_EMBEDDED &&
                result.runMode != RunMode.CREATE_ALL && 
                result.getInputFilenames().size() > 1){
            result.isOK = false;
            result.errorMsg = "Selected run mode allows only single input grammar file, but multiple grammar files were specified.";
        }
        if(result.customTargetDefinitionFile != null){
            File f = new File(result.customTargetDefinitionFile);
            if(!f.isFile()){
                result.isOK = false;
                result.errorMsg = String.format("Provided target definition file %s not found.",result.customTargetDefinitionFile);
            }
        }
        return result;
    }
    
    public String getInputFilename(){
        return inputFiles.get(0).getFilename();
    }
    
    public List<Arguments.InputFileArgumentGroup> getInputFilenames(){
        return inputFiles;
    }
    /**
     * Remove rightmost extension of filename
     * 
     * @param filename
     * @return filename without rightmost extension
     */
    private static String removeExtension(String filename){
        if(filename.contains(".")){
            int lastIndex = filename.lastIndexOf('.');
            return filename.substring(0, lastIndex);
        }else{
            return filename;
        }
    }
    
    /**
     * Get output filename
     * 
     * Returns filename specified by command line arguments.
     * If the filename was not specified returns name of the 
     * input pg file with desired extension.
     * 
     * @param extension 
     * @return output filename
     */
    public String getOutputFilename(String extension){
        if(outputFilename == null){
            File inputFile = new File(getInputFilename()).getAbsoluteFile();
            File directory = inputFile.getParentFile();
            String tmp = String.format("%s.%s",removeExtension(inputFile.getName()),extension);
            File outputFile = new File(directory.getAbsolutePath(),tmp);
            outputFilename = outputFile.getAbsolutePath();
        }
        return outputFilename;
    }
    
    public String getOutputDirname(){
        if( outputFilename == null){
            File inputParentDir = new File(getInputFilename()).getParentFile();
            outputFilename = inputParentDir.getAbsolutePath();
        }
        return outputFilename;
    }
    
    public Arguments.RunMode getRunmode(){
        return runMode;
    }
    
    public String getVendor(){
        return vendor;
    }
    
    public void setVersion(String version){
        if(version.endsWith("-SNAPSHOT") ){
            this.version = version.substring(0,
                    version.length() - "-SNAPSHOT".length());
            this.snapshot = true;
        }else{
            this.version = version;
        }
    }
    
    public String getVersion(){
        return version;
    }
    
    public String getXtextVersion(){
        return xtextVersion;
    }
    
    public String getXtextVersionNoSnapshot(){
        if(xtextVersion.endsWith("-SNAPSHOT")){
            return xtextVersion.substring(0, xtextVersion.length() - "-SNAPSHOT".length() );
        }else{
            return xtextVersion;
        }
    }
    
    public String getXtextUpdateSite(){
        return xtextUpdateSite;
    }
    
    public String getMavenPath(){
        return mavenPath;
    }
    
    public boolean isOK(){
        return isOK;
    }
    
    public boolean isSnapshot(){
        return snapshot;
    }
    
    public boolean isTargetDefinitionUse(){
        return targetDefinitionUse;
    }
    
    public boolean isIncludeAllDependencies(){
        return includeAllDependencies;
    }
    
    public String getTargetDefinitionFile(){
        return customTargetDefinitionFile;
    }
}