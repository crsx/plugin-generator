/* Copyright (c) 2015 IBM Corporation. */
package net.sf.crsx.pgutil.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ibm.icu.text.Transliterator;

public class Util {
    
    public static final int BUFFER_SIZE = 0x1000;
    
    private static final Transliterator transliterator = Transliterator.getInstance("Any-Name");
    
    private Util(){
        
    }
    
    /**
     * Copy whole content of InputStream to OutputStream
     * 
     * @param is source 
     * @param os destination
     * @return number of bytes copyied
     * @throws IOException
     */
    public static long copy(InputStream is, OutputStream os) throws IOException {
        if( is == null || os == null ){
            throw new IOException("Stream is null");
        }
        
        long copied = 0;
        byte [] buffer = new byte[BUFFER_SIZE];
        while( true ){
            int read = is.read(buffer);
            if( read < 0 ){
                break;
            }
            os.write(buffer,0,read);
            copied += read;
        }
        return copied;
    }

    public static String normalizeName(String name){
        String result = "";
        boolean makeUpper = true;
        for( int i = 0; i < name.length(); ++i ){
            char c = name.charAt(i);
            if( c == '-' || c == '_'){
                makeUpper = true;
            }else{
                if(makeUpper){
                    c = Character.toUpperCase(c);
                    makeUpper = false;
                }
                result+=c;
            }
        }
        return result;
    }

    public static String generateNamespaceURIForQualifiedName(String qualifiedName){
        if(!qualifiedName.contains(".")){
            return qualifiedName;
        }
        String [] parts = qualifiedName.split("\\.");
        
        String tail = "";
        for( int i = 2; i < parts.length; ++i ){
            tail += "/" + parts[i];
        }
        
        if( parts.length > 2){
            return String.format("http://www.%s.%s%s", parts[1],parts[0],tail);
        }else if ( parts.length > 1){
            return String.format("http://www.%s.%s",parts[1],parts[0]);
        }else{
            return String.format("http://www.%s.com", parts[0]);
        }
    }
    
    public static String escapeUnicode(String str){
        StringBuilder escapedStrBuilder = new StringBuilder();
        for( Character c : str.toCharArray() ){
            if( c > 128 ){
                escapedStrBuilder.append(String.format("\\u%04X",(int)c));
            }else{
                escapedStrBuilder.append(c);
            }
        }
        
        return escapedStrBuilder.toString();
    }
    
    public static String transformToName(String str,boolean upperCaseFirst){
        boolean first = true;
        boolean up = true;
        StringBuilder builder = new StringBuilder();
        for(Character c : str.toCharArray()){
            if(isValidAntlrIdentCharacter(c, first)){
                if(first && upperCaseFirst){
                    builder.append( upperCaseFirst ? Character.toUpperCase(c)
                            : Character.toLowerCase(c));
                }else{
                    builder.append(up ? Character.toUpperCase(c) : c);
                }
                first = false;
                up = false;
            }else{
                up = true;
            }
        }
        return builder.toString();
    }
    
    public static boolean isValidAntlrIdentCharacter(char c,boolean first){
        //TODO improve this
        boolean result;
        result = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || c == '_';
        if(!first){
            result = result || ( c>= '0' && c <= '9');
        }
        return result;
    }
    
    public static String getCharacterName(char c){
        String transliterated = transliterator.transliterate(String.valueOf(c));
        return transliterated.substring("\\N{".length(),transliterated.length()-"}".length());
    }
    
    public static String transliterate(String str){
        String result = transformToName(str,true);
        if( result.length() > 0){
            return result;
        }
        StringBuilder builder = new StringBuilder();
        for(Character c : str.toCharArray()){
            String transliteratedChar = getCharacterName(c);
            builder.append(transliteratedChar);
            builder.append(" ");
        }
        return transliterate(builder.toString().toLowerCase().trim());
    }
    
    public static String unquote(String str){
        if(str.length() >= 2){
            return str.substring(1,str.length()-1);
        }else{
            return str;
        }
    }
}
