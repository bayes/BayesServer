/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Compilers;
import java.util.*;
/**
 *
 * @author apple
 */
public class IntelCompiler {

     public static final List <String>  FORTRAN_COMPILERS_LINUX     = new ArrayList<String>();
     public static final  List <String>  C_COMPILERS_LINUX          = new ArrayList<String>();

     public static final List <String> C_COMPILERS                  = new ArrayList<String>();
     public static final List <String> F_COMPILERS                  = new ArrayList<String>();
     public  static final String FORTAN_SETUP_LINUX64               = "ifortvars_intel64.csh";
     public  static final String C_SETUP_LINUX64                    = "iccvars_intel64.csh";
     public  static final String FORTAN_SETUP_LINUX32 = "ifortvars_ia32.csh";
     public  static final String C_SETUP_LINUX32 = "iccvars_ia32.csh";


     /*Starting from recent intel compilers version   - compilers should be set uo differetnly
      * e.g. (for 64 bit)
      *
         source /opt/intel/composerxe-2011/bin/compilervars.csh intel64
      *
      * (replace "intel64" with "ia32" if you are using a 32-bit platform).
      *
      * I thinkg it should be the same for fortran or C (we only have Fortran at the moment (March 2001))
      */
     public static final String NEW_COMPILER_SETUP_SCRIPT                    = "compilervars.csh";
     public static final String NEW_COMPILER_SETUP_ARGUMRNT_64BIT            = "intel64";
     public static final String NEW_COMPILER_SETUP_ARGUMRNT_32BIT            = "ia32";
     static{


             //LINUX

             // ALL FORTRAN COMPILERS
             F_COMPILERS.add("ifort");

             // ALL C COMPILERS
             C_COMPILERS.add( "icc");

     }

     public static boolean isFCompiler(String name){
        return F_COMPILERS.contains(name);
     }
     public static boolean isCCompiler(String name){
        return C_COMPILERS.contains(name);
     }
     public static boolean is64BitCompiler(String name){
        return name.contains("64");
     }

 

   





}
