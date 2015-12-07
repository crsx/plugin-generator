/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.single;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.crsx.pgutil.common.FileProvider;
import net.sf.crsx.pgutil.pggrammar.PGGrammar;
import net.sf.crsx.pgutil.xtextgen.XtextGrammarGenerator;

public class SingleXtextGrammarFileProvider extends FileProvider{
    private static final Logger LOGGER = Logger.getGlobal();
    
    PGGrammar grammar;
    
    public SingleXtextGrammarFileProvider(PGGrammar grammar){
        this.grammar = grammar;
    }
    
    @Override
    public void writeFile(File file) {
        try{
            PrintStream printStream = new PrintStream( new FileOutputStream(file));
            writeToStream(printStream);
            printStream.close();
        }catch(FileNotFoundException exception){
            String errorMessage = String.format("File %s not found%n",file.getPath());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }
    }
    
    @Override
    public void writeFile(File file,ZipOutputStream zip){
        ZipEntry zipEntry = new ZipEntry(file.getPath());
        try {
            zip.putNextEntry(zipEntry);
        } catch (IOException exception) {
            String errorMessage = String.format("Error message creating zip entry %s", file.getPath());
            LOGGER.log(Level.SEVERE, errorMessage, exception);
        }
        PrintStream printStream = new PrintStream(zip);
        writeToStream(printStream);
    }
    
    private void writeToStream(PrintStream printStream){
        XtextGrammarGenerator generator = new XtextGrammarGenerator();
        generator.generateXtextGrammar(grammar, printStream);
    }

}
