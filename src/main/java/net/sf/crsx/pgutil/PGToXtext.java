/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import net.sf.crsx.pgutil.common.DirectoryZipper;
import net.sf.crsx.pgutil.common.Plugin;
import net.sf.crsx.pgutil.crsx.CRSXPlugin;
import net.sf.crsx.pgutil.multiple.MultiplePlugins;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.pggrammar.PGNonTerminal;
import net.sf.crsx.pgutil.single.SingleGrammarPlugin;
import net.sf.crsx.pgutil.xtextgen.XtextGrammarGenerator;
import net.sf.crsx.pgutil.pgparser.PGParser;
import net.sf.crsx.pgutil.pgparser.ParseException;

public final class PGToXtext {
    
    private static final String HELP_STR = "Usage: java -jar pgenerator.jar [options] inputfile.pg\n\n"
                                          +"Options:\n"
                                          +"-o  / --output       <output_location>\t Specify output location\n"
                                          +"-s  / --start        <NonterminalName>\t Specify starting nonterminal\n"
                                          +"-e  / --extension    <extension>\t\t Specify extension for the plugin\n"
                                          +"-v  / --version      <version>\t\t Specify version of generated plugins\n"
                                          +"-r  / --organization <vendor>\t\t Specify vendor of generated plugins\n"
                                          +"-m  / --maven        <maven_location>\t Specify path to maven directory\n"
                                          +"-g  / --single\t\t\t\t Create plugin for single grammar\n"
                                          +"-x  / --xtext\t\t\t\t Create just xtext grammar for source grammar\n"
                                          +"-c  / --crsx\t\t\t\t Create CRSX plugin with embedded grammar\n"
                                          +"-a  / --all\t\t\t\t Create CRSX plugin with support for embedded grammars and also plugins for embedded languages\n"
                                          +"-b  / --build\t\t\t\t Build generated sources using maven\n"
                                          +"-u  / --updatesite\t\t\t Produce Eclipse update site repository (using maven build)\n"
                                          +"-z  / --zip\t\t\t\t Create single zip archive\n"
                                          +"-xv / --xtextVersion\t\t\t Specify Xtext version to use\n"
                                          +"-xu / --xtextUpdateSite \t\t Specify Xtext update site to use\n"
                                          +"-sn / --snapshot \t\t\t Generate snapshot (default)\n"
                                          +"-ns / --nosnapshot \t\t\t Do not generate snapshot\n"
                                          +"-t  / --target\t\t\t\t Use target definition file for building (default)\n"
                                          +"-nt / --notarget \t\t\t Do not use target definition file for building\n"
                                          +"-tf / --targetfile <file> \t\t Specify custom target definition file\n"
                                          +"-d  / --include_dependencies \t\t Include all dependencies in update site\n"
                                          +"-nd / --no_include_dependencies \t Do not include dependencies in update site (default)\n"
                                          +"-h  / --help\t\t\t\t Show this help";
    
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    private static Arguments arguments;
    
    private PGToXtext(){
        
    }
    
    public static Arguments getArguments(){
        return arguments;
    }
    
    /**
     * Write Xtext grammar to PrintStream
     * 
     * @param printStream target
     * @param grammar source grammar
     */
    public static void writeXtext(PrintStream printStream, PGGrammar grammar){
        XtextGrammarGenerator xtextGenerator = new XtextGrammarGenerator();
        xtextGenerator.generateXtextGrammar(grammar, printStream);
        printStream.close();
    }
    
    /**
     * Parse PG grammar from stream into object representation
     * 
     * @param is input pg grammar
     * @return object representation of parsed grammar
     */
    public static PGGrammar parseGrammar(InputStream is){
        PGParser parser = new PGParser(is);
        
        PGGrammar grammar = null;
        try {
            grammar = parser.Grammar();
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Parser error", e);
            System.exit(-1);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException while parsing", e);
            System.exit(-1);
        }
        System.out.println("Grammar classname: "+grammar.getGrammarClassName());
        System.out.println("Grammar prefix: " + grammar.getPrefix());
        return grammar;
    }
    
    /**
     * Parse grammar from input file according to arguments given
     * 
     * The input file is taken from the arguments and so is starting symbol 
     * of the grammar. If the starting symbol is not specified, the utility
     * tries to guess starting symbol and will pick the only one which is 
     * never used on the right side of any rule. If such a nonterminal 
     * doesn't exist utility fails.
     * 
     * @param arguments Command line arguments
     * @return object representation of parsed grammar
     */
    public static PGGrammar getGrammar(Arguments.InputFileArgumentGroup argGroup){
        File inputFile = new File(argGroup.getFilename());
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, String.format("File %s not found",arguments.getInputFilename()));
            System.exit(-1);
        }
        
        PGGrammar grammar = parseGrammar(inputStream);
        if( argGroup.getRootNonTerminal() == null){
            System.out.println("Starting symbol was not explicitly specified.");
            System.out.println("Utility will try to guess starting symbol.");
            if( grammar.guessStartingNonTerminal() ){
                System.out.println(String.format("%s will be used as grammar starting symbol", 
                        grammar.getStartNonTerminal().getName()));
            }else{
                System.err.println("Utility failed to determine grammar starting symbol.");
                System.err.println("Please specify starting symbol explicitly by -s / --start argument");
                System.exit(-1);
            }
        }else{
            PGNonTerminal startNonTerminal = grammar.getNonTerminal(argGroup.getRootNonTerminal());
            if( startNonTerminal != null ){
                grammar.setStartNonTerminal(startNonTerminal);
            }else
            {
                System.err.println(String.format("Grammar does not contain %s nonterminal.", argGroup.getRootNonTerminal()));
            }
        }
        
        if(argGroup.isExtensionSet()){
            grammar.setExtension(argGroup.getExtension());
        }
        return grammar;
    }
    
    /**
     * Convert source pg grammar to single .xtext grammar file
     * 
     * @param arguments command line arguments
     */
    public static void convertSingleJustGrammar(Arguments arguments){
        
        PGGrammar grammar = getGrammar(arguments.inputFiles.get(0));
        
        String outputFilename = arguments.getOutputFilename("xtext");
        OutputStream os = null;
        try {
            os = new FileOutputStream(outputFilename,false);
            writeXtext(new PrintStream(os),grammar);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, String.format("File %s not found",outputFilename));
            System.exit(-1);
        }
        String msg = String.format("Output written to %s", outputFilename);
        System.out.println(msg);
    }
    
    /**
     * Executes `package` target against provided pom.xml file
     * 
     * @param pomXMLFile pom.xml file to execute
     * @param mavenPath  path to maven home directory
     * @return true if maven build finishes succesfully
     */
    public static boolean runMaven(File pomXMLFile, String mavenPath){
        System.out.println("Starting maven build...");
        
        InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(pomXMLFile);
        invocationRequest.setGoals( Collections.singletonList("package") );
        
        Invoker invoker = new DefaultInvoker();
        if(mavenPath != null){
            invoker.setMavenHome(new File(mavenPath));
        }
        try {
            InvocationResult invocationResult = invoker.execute(invocationRequest);
            if(invocationResult.getExitCode() != 0){
                String msg = String.format("Maven returned exit code %d",
                        invocationResult.getExitCode());
                Exception exception = invocationResult.getExecutionException();
                if(exception == null){
                    LOGGER.log(Level.SEVERE, msg);
                }else{
                    LOGGER.log(Level.SEVERE, msg,exception);
                }
                return false;
            }
        } catch (MavenInvocationException exception) {
            String err = "Failed to run maven";
            LOGGER.log(Level.SEVERE, err, exception);
            return false;
        }
        
        System.out.println("Maven build finished");
        return true;
    }
    
    /**
     * Generates source files and possibly builds plugin based on 
     * provided arguments
     * 
     * If the build mode was not explicitly set to build generated sources
     * or to produce only built updatesite, sources and only generated
     * and possibly zipped (if the --zip option was set).
     * 
     * If the build mode was set to --build, sources are generated to the
     * location provided by --output argument and maven build is then 
     * invoked on the parent project. This cannot be combined with the --zip
     * option.
     * 
     * If the build mode was set to --updatesite, sources are generated
     * to temporary location, maven build is invokend on the parent project
     * and after the build finishes, updatesite is copied (and possibly zipped,
     * if the --zip option was specified) to location provided by --output option
     * 
     * @param plugin Plugin object to process
     * @param arguments arguments object
     */
    public static void generateAndBuild(Plugin plugin,Arguments arguments){
        if(arguments.buildMode == Arguments.BuildMode.NO_BUILD){
            if(!arguments.zip){
                File outputDirectory = new File(arguments.getOutputDirname());
                plugin.generatePluginSources(outputDirectory);
            }else{
                
                String outputFilename = arguments.getOutputFilename("zip");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(outputFilename));
                    ZipOutputStream zip = new ZipOutputStream(fileOutputStream);
                    plugin.generatePluginSources(zip);
                    zip.flush();
                    zip.close();
                } catch (FileNotFoundException exception) {
                    String errorMessage = String.format("Path %s not found",outputFilename);
                    LOGGER.log(Level.SEVERE,errorMessage,exception);
                } catch (IOException exception) {
                    String errorMessage = String.format("Error finalizing zip archive");
                    LOGGER.log(Level.SEVERE,errorMessage, exception);
                }
            }
        }else if (arguments.buildMode == Arguments.BuildMode.BUILD_GENERATED_SOURCES){
            if(arguments.zip){
                System.err.println("Zipping of build sources is not suppported");
                System.exit(-1);
            }
            File outputDirectory = new File(arguments.getOutputDirname());
            plugin.generatePluginSources(outputDirectory);
            
            File parentPOMFile = new File(outputDirectory,plugin.getParentPOMFile().getPath());
            if(!runMaven(parentPOMFile,arguments.getMavenPath())){
                System.exit(-1);
            }
        }else if (arguments.buildMode == Arguments.BuildMode.BUILD_UPDATESITE_ONLY){
            File outputDirectory = null;
            try {
                outputDirectory = Files.createTempDirectory("pgutil").toFile();
                String msg = String.format("Source will be generated into temporary directory  %s",
                        outputDirectory.getAbsolutePath());
                LOGGER.log(Level.INFO, msg);
            } catch (IOException exception) {
                String err = "Failed to create temporary directory";
                LOGGER.log(Level.SEVERE, err,exception);
                System.exit(-1);
            }
            
            plugin.generatePluginSources(outputDirectory);
            
            File parentPOMFile = new File(outputDirectory,plugin.getParentPOMFile().getPath());
            if(!runMaven(parentPOMFile,arguments.getMavenPath())){
                System.exit(-1);
            }
            
            File tmpDirectory = outputDirectory;
            outputDirectory = new File(arguments.getOutputDirname());
            File eclipseRepoDir = new File(tmpDirectory,plugin.getEclipseRepoPath().getPath());
            
            if(!arguments.zip){
                eclipseRepoDir.renameTo(outputDirectory);
            }else{
                File outputFile = new File(arguments.getOutputFilename("zip"));
                DirectoryZipper dirZipper = new DirectoryZipper(eclipseRepoDir,outputFile);
                try {
                    dirZipper.process();
                } catch (IOException exception) {
                    String msg = String.format("Error zipping %s to %s", 
                            eclipseRepoDir.getAbsolutePath(),
                            outputFile.getAbsolutePath());
                    LOGGER.log(Level.SEVERE,msg,exception);
                    System.exit(-1);
                }
            }
        }
    }
    
    /**
     * Convert source pg grammar to plugin supporting single language 
     * defined in source pg grammar
     * 
     * @param arguments command line arguments
     */
    public static void convertSingle(Arguments arguments){
        
        PGGrammar grammar = getGrammar(arguments.inputFiles.get(0));
        
        SingleGrammarPlugin plugin = new SingleGrammarPlugin(grammar);
        generateAndBuild(plugin,arguments);
    }
    
    /**
     * Create CRSX plugin with embedded support for language specified by
     * pg grammar
     * 
     * @param arguments command line arguments
     */
    private static void createCRSXPlugin(Arguments arguments) {
        
        CRSXPlugin plugin = new CRSXPlugin();
        
        for(Arguments.InputFileArgumentGroup argGroup : arguments.inputFiles ){
            PGGrammar embeddedGrammar = getGrammar(argGroup);
            plugin.addEmbeddedGrammar(embeddedGrammar);
        }
        
        generateAndBuild(plugin,arguments);
    }
    
    /**
     * Create single plugin for each of the input grammars and 
     * also create CRSX plugin with each of the grammars embedded
     * 
     * @param arguments command line arguments
     */
    private static void createMultiple(Arguments arguments) {
        
        List<Plugin> plugins = new LinkedList<Plugin>();
        CRSXPlugin crsxPlugin = new CRSXPlugin();
        
        for(Arguments.InputFileArgumentGroup argGroup : arguments.inputFiles ){
            PGGrammar grammar = getGrammar(argGroup);
            SingleGrammarPlugin singlePlugin = new SingleGrammarPlugin(grammar);
            plugins.add(singlePlugin);
            crsxPlugin.addEmbeddedGrammar(grammar);
        }
        
        //TODO Creating crsx plugin modifies grammar object (adds meta/inject productions)
        //This should be changed so the order in which projects are generated wouldn't 
        //matter
        plugins.add(crsxPlugin);
        
        MultiplePlugins multiplePlugin = new MultiplePlugins(plugins);
        
        generateAndBuild(multiplePlugin,arguments);
    }
    
    public static void printHelp(){
        System.out.println(HELP_STR);
    }

    public static void main(String[] args) {
        arguments = Arguments.parseArgs(args);
        
        if( arguments.isOK() ){
            switch( arguments.getRunmode() ){
                case SHOW_HELP:
                    printHelp();
                    break;
                case CRSX_EMBEDDED:
                    createCRSXPlugin(arguments);
                    break;
                case CONVERT_SINGLE_JUST_GRAMMAR:
                    convertSingleJustGrammar(arguments);
                    break;
                case CONVERT_SINGLE:
                    convertSingle(arguments);
                    break;
                case CREATE_ALL:
                    createMultiple(arguments);
                    break;
                default:
                    //Do nothing
            }
        }else{
            if( arguments.errorMsg != null ){
                String errMsg = String.format("Error in arguments: %s", arguments.errorMsg);
                System.err.println(errMsg);
            }
            printHelp();
        }
        
    }

}
