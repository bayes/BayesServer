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
public class GnuCompiler {

     public static final List <String> C_COMPILERS                  = new ArrayList<String>();
     public static final List <String> F_COMPILERS                  = new ArrayList<String>();

      static{


            String gfortran  = "gfortran";
            String gcc  = "gcc";


             F_COMPILERS.add(gfortran);
             C_COMPILERS.add( gcc );


     }


    
     public static boolean isCCompiler(String name){
        return  C_COMPILERS.contains(name);
     }
     public static boolean isFCompiler(String name){
        return F_COMPILERS.contains(name);
     }


}
