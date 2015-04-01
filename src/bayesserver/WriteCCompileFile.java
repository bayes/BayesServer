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
public class WriteCCompileFile {
    public static final String INTEL_C_OPTIONS  = "echo -O3 -fPIC -shared -lm ";
    public static final String GNU_C_OPTIONS    = "echo -O3 -fPIC -shared -lm ";
    public static final String SUN_C_OPTIONS    = "echo -O3 -fPIC -shared -lm ";
    public static  String writeContent(   Installer installer){

        StringBuilder sb        =   new StringBuilder();
        String ccompiler        =   installer.getcCompilerName();

        boolean isIntel         =   IntelCompiler.isCCompiler(ccompiler);
        if (isIntel ){ sb.append(INTEL_C_OPTIONS + "\n"); }

        boolean isGNU           =   GnuCompiler.isCCompiler(ccompiler);
        if (isGNU ){ sb.append(GNU_C_OPTIONS     + "\n"); }

        boolean isSun           =   SunCompiler.isCCompiler(ccompiler);
        if (isSun  ){ sb.append(SUN_C_OPTIONS    + "\n"); }
          

        return sb.toString();


    }
}
