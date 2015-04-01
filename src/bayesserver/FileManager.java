/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import java.io.*;
import java.util.Date;

/**
 *
 * @author apple
 */
public class FileManager implements Constants {
       public static  final String USER_HOME_DIR              =  System.getProperty("user.home");
       public static  final String USER_CURRENT_DIR           =  System.getProperty( "user.dir");

       public static final String HTACESS                     =  "htaccess";
       public static final String SYSTEMDIR                   =  "system";
       public static final String CGI_BIN                     =  "cgi-bin";
       public static final String BIN                         =  "bin";
       public static final String SBIN                        =  "sbin";
       public static final String LIB                         =  "lib";
       public static final String BAYES                       =  "Bayes";
       public static final String BAYESJAR                    =  "Bayes.jar";
       public static final String UTILITIES_DIR               =  "utilities";
       public static final String CSHRC                       =  ".cshrc";
       public static final String LOGIN                       =  ".login";
       public static final String JNLP                        =  "launch.jnlp";
       public static final String FORTANSETUP                 =  "FortranSetup";
       public static final String FCOMPILE                    =  "Bayes_compile_f";
       public static final String CCOMPILE                    =  "Bayes_compile_c";
       public static final String JAVAOUT                     =  "javaout.txt";
       public static final String JAVAERR                     =  "javaerr.txt";
       public static final String INSTALLATION_PROPERTIES     =  "install.props";





       public  static File getInstallationInfoFile(){
            File  dir           =    getBayesDir();
            File file           =   new File(dir, Constants.INSTALLATION_INFO_FILE);
            return file;
        }
        public  static File getInstallationPropertiesFile(){
            File  dir           =    getBayesDir();
            File file           =   new File(dir, INSTALLATION_PROPERTIES);
            return file;
        }
       public  static File getHTAccessFile(){
            File  dir           =   getCgiBinDir();
            File file           =   new File(dir, HTACESS);
            return file;
        }
       public  static File getHiddenHTAccessFile(){
            File  dir           =   getCgiBinDir();
            File file           =   new File(dir, "."+HTACESS);
            return file;
        }
       public  static File getUsersFile(){
            File  dir           =   getSystemDir();;
            File file           =   new File(dir, "users");
            return file;
        }
       public  static File getBayesJARFile(){
            File  dir           =   getBayesDir();
            File file           =   new File(dir, BAYESJAR );
            return file;
       }
       public  static File getJavaOutFile(){
            File  dir           =   getSystemDir();
            File file           =   new File(dir, JAVAOUT);
            return file;
        }
       public  static File getJavaErrFile(){
            File  dir           =   getSystemDir();
            File file           =   new File(dir, JAVAERR );
            return file;
        }



       public static File getSystemDir(){
            File file           =   new File(USER_HOME_DIR, SYSTEMDIR );
            return file;
        }
       public static File getCgiBinDir(){
        File file  = new File (USER_HOME_DIR, CGI_BIN );
        return file;
     }
       public static File getBinDir(){
        File file  = new File (USER_HOME_DIR, BIN);
        return file;
     }
       public static File getSBinDir(){
        File file  = new File (USER_HOME_DIR, SBIN);
        return file;
     }
       public static File getLibDir(){
        File file  = new File (USER_HOME_DIR, LIB);
        return file;
      }
       public static File getBayesDir(){
        File file  = new File (USER_HOME_DIR, BAYES);
        return file;
      }
       public static File getUtilitiessDir(){
        File file  = new File (USER_HOME_DIR, UTILITIES_DIR );
        return file;
      }
       public static File getCSHRCFile(){
        File file  = new File (USER_HOME_DIR, CSHRC);
        return file;
      }
       public static File getLoginFile(){
        File file  = new File (USER_HOME_DIR, LOGIN);
        return file;
      }
       public static File getJnlpFile(){
        File file  = new File ( getBayesDir(), JNLP);
        return file;
      }
     
      
       public static File getFortranSetupFile(){
           File dir   =  getSBinDir();
           File file  = new File (dir, FORTANSETUP );
        return file;
      }
       public static File getFCompileFile(){
           File dir   =  getSBinDir();
           File file  = new File (dir,FCOMPILE  );
        return file;
      }
       public static File getCCompileFile(){
           File dir   =  getSBinDir();
           File file  = new File (dir,CCOMPILE  );
        return file;
      }

       public static void main(String [] args){

        File f = new File("/Users/apple/Test");
        try {
            f.createNewFile();
           // System.out.println(f.getAbsolutePath());
            f.setReadOnly();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

       }

       public static void  redirectStandardOut(){
        File f  = FileManager.getJavaOutFile();

        try{
        if (f.exists() == false){
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        
             System.setOut(new PrintStream( new FileOutputStream( f)));
        }
        catch(Exception exp){

        }
    }
       public static void  redirectStandardErr(){
        File f  = FileManager.getJavaErrFile();
        try{
            if (f.exists() == false){
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            System.setErr(new PrintStream( new FileOutputStream( f)));
        }
        catch(Exception exp){

        }
    }
       public static void  redirectStandard(){
        redirectStandardOut();
        redirectStandardErr();
    }

       

}
