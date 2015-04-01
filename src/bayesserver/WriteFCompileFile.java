/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;

import Compilers.GnuCompiler;
import Compilers.IntelCompiler;
import Compilers.SunCompiler;

/**
 *
 * @author apple
 */
public class WriteFCompileFile {
    public static final String INTEL_F_OPTIONS  = "echo  -132 -O3 -openmp -nbs -fpp -i_dynamic -ip -shared -fPIC ";
    public static final String GNU_F_OPTIONS    = "echo -shared -fPIC -fopenmp -ffixed-line-length-none ";
    public static final String SUN_F_OPTIONS    = "echo -xtarget=generic -dalign -O5 -e -vpara -openmp -explicitpar -stackvar -G -pic -ztext -h ";
 
    public static  String writeContent(   Installer installer){

        StringBuilder sb        =   new StringBuilder();
        String compiler        =   installer.getFortanCompilerName();
        System.out.println("Fortran compiler "+  compiler);

        boolean isIntel         =   IntelCompiler.isFCompiler(compiler );
        if (isIntel ){ sb.append(INTEL_F_OPTIONS + "\n"); }
          System.out.println(" isIntel  "+  isIntel );

        boolean isGNU           =   GnuCompiler.isFCompiler(compiler );
        if (isGNU ){ sb.append(GNU_F_OPTIONS     + "\n"); }
         System.out.println("  isGNU  "+  isGNU );

        boolean isSun           =   SunCompiler.isFCompiler(compiler );
        if (isSun  ){ sb.append(SUN_F_OPTIONS      + "\n"); }
         System.out.println(" isSun  "+ isSun  );


        return sb.toString();


    }
}
