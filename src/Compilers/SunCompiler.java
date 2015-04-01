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
public class SunCompiler {


     public static final List <String> C_COMPILERS                  = new ArrayList<String>();
     public static final List <String> F_COMPILERS                  = new ArrayList<String>();


     static{

        
             // ALL FORTRAN COMPILERS
             F_COMPILERS.add( "f95");
             F_COMPILERS.add( "f90");

             // ALL C COMPILERS
             C_COMPILERS.add("cc");

     }

     public static boolean isFCompiler(String name){
        return F_COMPILERS.contains(name);
     }
     public static boolean isCCompiler(String name){
        return C_COMPILERS.contains(name);
     }




}
