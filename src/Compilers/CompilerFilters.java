/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Compilers;

import java.io.File;
import java.util.*;

/**
 *
 * @author apple
 */
public class CompilerFilters {
         public static class CFileFilter extends javax.swing.filechooser.FileFilter{
         public  boolean	accept(File f) {
            if (f.isDirectory() )           { return true;}

            for (String string : IntelCompiler.C_COMPILERS ) {
               if ( f.getName().equals(string)){ return true;}
            }
            for (String string : GnuCompiler.C_COMPILERS ) {
               if ( f.getName().equals(string)){ return true;}
            }
             for (String string : SunCompiler.C_COMPILERS ) {
               if ( f.getName().equals(string)){ return true;}
            }



            return false;

         }
         public String	getDescription() {return "C compiler";}
    }
         public static class FortranFileFilter extends javax.swing.filechooser.FileFilter{
         public  boolean	accept(File f) {
            if (f.isDirectory() )           { return true;}

            for (String string : IntelCompiler.F_COMPILERS ) {
               if ( f.getName().equals(string)){ return true;}
            }
            for (String string : GnuCompiler.F_COMPILERS ) {
               if ( f.getName().equals(string)){ return true;}
            }
            for (String string : SunCompiler.F_COMPILERS ) {
               if ( f.getName().equals(string)){ return true;}
            }


            return false;

         }
         public String	getDescription() {return "Fortran compiler";}
    }
}
