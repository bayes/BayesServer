/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
/**
 *
 * @author apple
 */
public class WriteLogin {



     public static  String writeContent(    String binDir,
                                            String libDir,
                                            String javawsDir,
                                            String fortanSetupPath){
        StringBuilder sb    =   new StringBuilder();


     
        int avaliableProcessors = Runtime.getRuntime().availableProcessors();
        sb.append("setenv OMP_NUM_THREADS "+avaliableProcessors);
        sb.append("\n");
        sb.append("\n");

        String curpath;
        
        curpath = binDir;
        if (curpath!=null && curpath.isEmpty() == false ){
            sb.append("set path=("+ curpath +")"             + "\n");
        }
        
        sb.append("set path=($path /bin)"               + "\n");
        sb.append("set path=($path /usr/bin)"           + "\n");
        sb.append("set path=($path /usr/sbin)"          + "\n");
        sb.append("set path=($path /etc)"               + "\n");
        sb.append("set path=($path /usr/local/bin)"     + "\n");
        
        
        curpath = javawsDir;
        if (curpath!=null && curpath.isEmpty() == false ){
            sb.append("set path=($path "+ curpath +")"             + "\n");
        }
        
        sb.append("set path=($path .)"                  + "\n");


        sb.append("\n");

        sb.append("\n");
        sb.append("set host = `hostname`\n");

        sb.append("\n");

       
        sb.append("set term=xterm"+ "\n");
       
        sb.append("\n");

        sb.append("set username=`id | tr '()' '  ' | cut -f2 -d' '`"+ "\n");
        sb.append("set prompt=\"$username \\!>\""+ "\n");

        sb.append("\n");

        sb.append("setenv LD_LIBRARY_PATH /lib:/usr/lib:/usr/local/lib:" + libDir+":.");
        sb.append("\n");
        sb.append("\n");

        sb.append("source "+ fortanSetupPath);
        sb.append("\n");

        sb.append("source .cshrc"+ "\n");

        return sb.toString();


    }
}
