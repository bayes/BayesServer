/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import utilities.*;
import java.io.*;
import  java.util.Scanner;
/**
 *
 * @author apple
 */
public class WriteJnlpFile {

     public static final String APPLICATION_START_KEY        =  "<application-desc";
     public static final String APPLICATION_END_KEY          =  "</application-desc>";
     public static final String ARGUMENT_START_KEY           =  "<argument>";
     public static final String ARGUMENT_END_KEY             =  "</argument>";
     public static final String CODEBASE_KEY                 =   "codebase";



     public static  String writeContent( Installer installer, File src){
        StringBuilder sb            =       new StringBuilder();
        String string               =       IO.readFileToString(src );
        Scanner scanner             =       new Scanner(string);
        int availableProcessors     =       Runtime.getRuntime().availableProcessors();


        while (scanner.hasNextLine()){
                String line            =   scanner.nextLine();
               

                if (line.contains(APPLICATION_START_KEY) ){

                     if (line.contains(APPLICATION_END_KEY)){
                         //remove ending tag
                         line          = line.replace(APPLICATION_END_KEY, "");
                     }
                     else {
                            while (scanner.hasNextLine()){
                                String endline           =   scanner.nextLine();
                                if ( endline .contains(APPLICATION_END_KEY)){
                                    break;
                                }
                              }
                      }


                     int start          =   line.indexOf(APPLICATION_START_KEY);
                     String pad         =   line.substring(0, start);

                     sb.append(line);
                     sb.append("\n");


                     // hostname
                     addArgument(sb, pad,"server hostname", installer.serverHostName );
                     
                     // is password protected
                     addArgument(sb, pad,"is password protected?", installer.isPasswordProtected());
                 
                     // number of CPU
                     addArgument(sb, pad,"number of available CPUs", availableProcessors);

                     // processing account
                     addArgument(sb, pad,"processing account", installer.getUserName());

                     //port
                     addArgument(sb, pad,"server port", installer.getPort());
                 
                     //is Fortan Compiler
                     addArgument(sb, pad,"is Fortan compiler installed", installer.isFortanCompilerSet());
     
                     //is C Compiler
                     addArgument(sb, pad,"is C compiler installed",installer.isCCompilerSet());

                     //queue
                     addArgument(sb, pad,"server queue",installer.getServerQueue());


                     sb.append(pad);
                     sb.append(APPLICATION_END_KEY);
                     sb.append("\n");

                }
                else if (line.contains(CODEBASE_KEY) ){
                    line =  writeCodeBase(installer, line);
                    sb.append(line);
                    sb.append("\n");
                }

                else{
                    sb.append(line);
                    sb.append("\n");
                }



       }
        return sb.toString();
    }

   public static void addArgument(StringBuilder sb, String pad , String comment, Object value){
                     String extrapad    =   pad + "   ";

                     sb.append(extrapad);
                     sb.append("<!-- "+ comment+ " -->");
                     sb.append("\n");
                     sb.append(extrapad);
                     sb.append(ARGUMENT_START_KEY );
                     sb.append(value);
                     sb.append(ARGUMENT_END_KEY);
                     sb.append("\n");

   }
   public static String writeCodeBase(Installer installer, String line){
    int keyStartIndex       =   line.indexOf(CODEBASE_KEY);
    int keyStartEnd         =   keyStartIndex + CODEBASE_KEY.length();
    int startIndex          =   line.indexOf("\"", keyStartEnd )+ 1;
    int endIndex            =   line.indexOf("\"", startIndex );

    String curCodeBase      =   line.substring(startIndex  , endIndex);
    String newCodeBase      =   "http://"+installer.getServerIP()+":"+installer.getPort()+"/"+FileManager.BAYES;
    String newLine          =   line.replace( curCodeBase , newCodeBase);

    return newLine;

   }

   public static void main(String [] atr){
Installer installer =new Installer();
    String line = "<jnlp codebase=\"http://bmrw206.wustl.edu:8080/Bayes/\" href=\"launch.jnlp\" spec=\"1.0+\">";
    int keyStartIndex   = line.indexOf(CODEBASE_KEY);
    int keyStartEnd     = keyStartIndex + CODEBASE_KEY.length();
    int startIndex      = line.indexOf("\"", keyStartEnd )+ 1;
    int endIndex       = line.indexOf("\"", startIndex );

    String curCodeBase  =   line.substring(startIndex  , endIndex);
    String newCodeBase  =   "http://"+installer.serverHostName+":"+installer.getPort()+"/"+FileManager.BAYES;
    String newLine      =   line.replace( curCodeBase , newCodeBase);

       System.out.println(newLine );

   }
}
