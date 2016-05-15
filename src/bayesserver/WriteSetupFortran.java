/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;

import Compilers.IntelCompiler;

/**
 *
 * @author apple
 */
public class WriteSetupFortran {
    public static final String SOURCE       =    "source";
    public static final String ECHO         =    "echo";
    public static final String PATH         =    "PATH";
    public static final String INTEL64      =    "intel64";
    public static final String IFORT        =    "ifort";
    public static final String SETENV       =    "setenv";
    public static final String $PATH        =    "${PATH}";

     public static  String writeContent(   BaseInstaller installer){
   
        StringBuilder sb        =   new StringBuilder();
        
        
        sb.append(writeC( installer));
        sb.append(writeFortan ( installer));
        sb.append(writeEcho( installer));

        return sb.toString();


    }
     public static  StringBuilder writeFortan(   BaseInstaller installer){
        StringBuilder sb        =   new StringBuilder();
        String fcompiler        =   installer.getFortanCompilerName();
        String script           =   installer.getFortanConfigSrcipt();
        boolean isFCompiler     =   installer.isFortanCompilerSet();
        boolean isIntel         =   IntelCompiler.isFCompiler(fcompiler);
        boolean is64bit         =   installer.is64Bit();

       if(isFCompiler && isIntel) {
             String src      = SOURCE +" "+  installer.getFortanConfigAbsolutePath();

             if (script.equalsIgnoreCase(IntelCompiler.NEW_COMPILER_SETUP_SCRIPT)){
                if (is64bit  ){
                    src      =  src + "  "+ IntelCompiler.NEW_COMPILER_SETUP_ARGUMRNT_64BIT;
                }
                else{
                    src      =  src + "  "+ IntelCompiler.NEW_COMPILER_SETUP_ARGUMRNT_32BIT;
                }
            }

            sb.append(src);
            sb.append( "\n");
            
        }
        else if(isFCompiler){
            String path  =   installer.getFortanDir()    + ":";
            sb.append(SETENV  + " "+ PATH  + " "+  path+$PATH);
            sb.append( "\n");
        }
        else {
            sb.append( "\n");
        }
        return sb;
    }
     public static  StringBuilder writeC(   BaseInstaller installer){
        StringBuilder sb        =   new StringBuilder();
        String ccompiler        =   installer.getcCompilerName();
        String script           =   installer.getcConfigSrcipt();
        boolean isCCompiler     =   installer.isCCompilerSet();
        boolean isIntel         =   IntelCompiler.isCCompiler(ccompiler);
        boolean is64bit         =   installer.is64Bit();
        

        if(isCCompiler && isIntel) {
             String src      = SOURCE +" "+  installer.getCConfigAbsolutePath();

             if (script.equalsIgnoreCase(IntelCompiler.NEW_COMPILER_SETUP_SCRIPT)){
                if (is64bit  ){
                    src      =  src + "  "+ IntelCompiler.NEW_COMPILER_SETUP_ARGUMRNT_64BIT;
                }
                else{
                    src      =  src + "  "+ IntelCompiler.NEW_COMPILER_SETUP_ARGUMRNT_32BIT;
                }
            }

            sb.append(src);
            sb.append( "\n");
        }
        else if(isCCompiler){
            String path  =   installer.getcDir()    + ":";
            sb.append(SETENV  + " "+ PATH  + " "+ path+$PATH);
            sb.append( "\n");
        }
        else{
             sb.append( "\n");
        }
        return sb;
    }
     public static  StringBuilder writeEcho( BaseInstaller installer){
        StringBuilder sb        =   new StringBuilder();
        sb.append(  ECHO );
        sb.append(  " "+  installer.getFortanCompilerName());
        sb.append(  " "+  installer.getcCompilerName());
        sb.append( "\n");

        return sb;
    }
}
