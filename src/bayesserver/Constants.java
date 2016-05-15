/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import java.io.*;
/**
 *
 * @author apple
 */
public interface Constants {
     public  static enum INSTALLATION {
        START           ("Start installation"                ),
        DOWNLOAD        ("Downloading installation kit"),
        UNARCHIVE       ("Uncompressing installation files"),
        INSTALL         ("Installing"),
        COMPLETE        ("Installation is complete"),
        FAILED          ("Installation Failed" );
        private String message;
        public static final int max                   =   100;
        public static final int min                   =   0;
        INSTALLATION (String aMessage){
            this.message = aMessage;

        }

        public String getMessage() {
            return message;
        }


    }
     public static enum PLATFORM {
         UNKNOWN       (   "unknown" ),
         SUN           (   "sun4"),
         LINUX         (   "LinuxPc" );

        final String keyName;
        final static int defaultPort = 8080;

        public static final String  SUNOS                   =   "Solaris";
        public static final String  CENTOS                  =   "CentOS";
        public static final String  REDHAT                  =   "Red Hat";
        public static final String  FEDORA                  =   "Fedora";
        public static final String  UBUNTU                  =   "Ubuntu";
        public static final String [] LINUX_OS              = 
                new String []{CENTOS , REDHAT,  FEDORA, UBUNTU  };
        public static final double  VALID_SUN_OSVERSION     =   9.0;
        public static final double  VALID_LINUX_RED_HAT_OSVERSION   =   4.7;
        public static final String  RUN_LEVEL_DIR_LINUX     =  "/etc/rc5.d";
        public static final String  RUN_LEVEL_DIR_SUN       =  "/etc/rc2.d";


       // httpd file - don't confuse with httpd executable


        public static final String []HARDWARE               =   { "x86", "x86_64", "i686", "sun4"};

        PLATFORM (String aname) {
            this.keyName       =    aname;
 
        }

        public String getName() {return keyName;}

        public String getHttpdFilename(int port){
            String name   = null;
            switch (this){
                case SUN     : name = "S95httpd"    +   port; break;
                case LINUX : name = "S85httpd"    +   port; break;

            }
            return name;
        }
        public String getConfFilename(int port){
            String name   = null;
            switch (this){
                case SUN     : name = "S95httpd"  +   port+ ".conf"; break;
                case LINUX : name = "httpd"       +   port+ ".conf"; break;

            }
            return name;
        }
        public String getInstallatonStartStopFilename(){
            return getHttpdFilename(defaultPort);
        }
        public String getInstallatotConfFilename(){
            return getConfFilename(defaultPort);
        }

        public boolean  isValid(String name){
            if (name == null|| name.length() == 0){return false;}

            boolean isValid  = false;
            switch (this){
                case SUN     : isValid =   isValidSun(name); break;
                case LINUX : isValid =   isValidLinux(name); break;

            }

            return isValid;
        }
        private boolean isValidSun(String name){
            // name should be something like : Solaris 9 9/05 s9s_u8wos_05 SPARC
            if (name.contains("Solaris" ) == false){
                return false;
            }
            else if (   name.contains("SPARC")== false 
                        && 
                        name.contains("Sparc")==false){
                        return false;
            }
            
            
      /*
            
            java.util.Scanner scanner = new java.util.Scanner(name);

            // check os name
            if (scanner.hasNext()){
                String osname = scanner.next();
                if (osname.equalsIgnoreCase(SUNOS) == false){return false;}
            }
            else { return false;}
               
           

            // check os version
            if (scanner.hasNextDouble()){
                double osversion = scanner.nextDouble();
                if ( osversion < VALID_SUN_OSVERSION){return false;}
            }
            else { return false;}
             */
            return true;
        }
        private boolean isValidLinux(String release){
            for (String os : LINUX_OS) {
                 if (release.contains(os)){return true;}
            }
            return false;
        }
    
     
        public  File    findVirginStartStopFile(){
            File file  =   null;
             switch (this){
                case SUN     :  file =  findVirginStartStopFileSun();break;
                case LINUX :  file =  findVirginStartStopFileLinux(); break;
            }

           return file;
        }
        public  File    findVirginStartStopFileLinux(){
            File virginServerHttpdFile  =   null;
            FilenameFilter filter       =   getStartStopFilenameFilter();
            File dir                    =   new File("/etc/rc5.d");

            if ( dir.exists() == false || dir.isDirectory()== false){
                   return virginServerHttpdFile;
            }

            File [] files =dir .listFiles(filter);
            if (files.length < 1 ){ return virginServerHttpdFile;}
                
            virginServerHttpdFile = files[0];


           return virginServerHttpdFile;
        }
        public  File    findVirginStartStopFileSun(){
           File file  =  new File ("/usr/apache/bin/apachectl");
           if (file.exists() == false){return null;}
           return  file;
        }
        public  String  getRunLevelDirecotries(){
             switch (this){
                case SUN     :  return RUN_LEVEL_DIR_SUN ;
                case LINUX :  return RUN_LEVEL_DIR_LINUX ;
                 default     :  return RUN_LEVEL_DIR_LINUX;
            }

        }
        public  FilenameFilter getStartStopFilenameFilter(){
            FilenameFilter filter;
             switch (this){
                case SUN     :  filter = new StartStopSunFilenameFilter  ();break;
                case LINUX :  filter = new StartStopLinuxFilenameFilter(); break;
                default      :  filter = null; break;
            }
            return filter;

        }
        public class StartStopLinuxFilenameFilter implements FilenameFilter{
            public boolean accept(File dir, String name){
                // modified July 26/2010
                //if (name.startsWith("K") == false){return false;}
                if (name.contains("httpd")){return true;}
                else {return false;}
            }
        }
        public class StartStopSunFilenameFilter implements  FilenameFilter{
            public boolean accept(File dir, String name){
                if (name.startsWith("K") == false){return false;}
                if (name.contains("appache")){return true;}
                else {return false;}
            }
        }


        public  File    findVirginConfFile(){
            String  confFile                =   getVirginConfFile();

            if ( confFile == null){return null;}
            File cnf                        =   new File (confFile);
            if (cnf.exists() == false) {return null;}
            if (cnf.isFile() == false) {return null;}

            return cnf;

        }
        public  String  getVirginConfFile(){
            String confFile     = null;
             switch (this){
                case SUN     :  confFile = "/etc/apache/httpd.conf-example";break;
                case LINUX :  confFile = "/etc/httpd/conf/httpd.conf";break;
            }
            return confFile;

        }


        public  String  getDestinationDirForConfFile(){
            String confFile     = null;
             switch (this){
                case SUN     :  confFile = "/etc/apache";break;
                case LINUX :  confFile = "/etc/httpd/conf";break;
            }
            return confFile;

        }
        public  String  getDestinationDirForStartStopFile(){
            String out     = null;
             switch (this){
                case SUN     :  out = "/etc/rc3.d";break;
                case LINUX :  out =  RUN_LEVEL_DIR_LINUX;break;
            }
            return out;


        }


        public String getCompilerDirGuess(){
            String compilerDir = null;
            switch (this){
                case SUN     :  compilerDir = "/opt/intel/Compiler/";break;
                case LINUX :  compilerDir = "/opt/intel/Compiler/"  ;break;
            }
            return compilerDir;

        }

     }



     public static final java.awt.Color BGColor             =   new  java.awt.Color(150, 150,150);

     public static final String OS_NAME                         = "os.name";
     public static final String USER_NAME                       = "user.name";
     public static final String NOCOMPILER                      = "None";
     public static final String DEFAULT_QUEUE                   = "None";
     public static final String INSTALLATION_INFO_FILE          =  "installation.inf";
     public static final String BMRL_MASK                       = "128.252.91.";
     public static final String LOOPBACK_IP                     = "127.0.0.1";

     
     // document root 
     public static final String DOCUMENTROOT_LINUXPC             = "/var/www";
     public static final String DOCUMENTROOT_SUN4                = "/var/apache";

     // httpd exe 
     public static final String HTTPD_EXECUATBLE_LINUXPC          = "/usr/sbin/httpd";
     public static final String HTTPD_EXECUATBLE_SUN4               = "/usr/apache/bin/httpd";

     // PIDFILENAME 
     public static final String  PIDFILE_DIR_LINUX               =   "/etc/httpd/run";
     public static final String  PIDFILE_DIR_SUN4                   =   "/var/run/";
         
     // logs
     public static final String  LOGS_DIR_LINUX                =   "/etc/httpd/logs";
     public static final String  LOGS_DIR_SUN4                 =   "/var/apache/logs";


     // Server Root
     public static final String  SERVERROOT_LINUXPC            =   "/etc/httpd";
     public static final String  SERVERROOT_SUN4               =   "/usr/apache";

  
}
