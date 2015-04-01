/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import utilities.*;
import java.io.*;
import  java.util.Scanner;
/**
 *
 * @author apple
 */
public class ModifyConfigurationFiles  implements Constants{
        public static final String MODIFYTAG        = "#modified";
        public static final String PORT8080         = "8080";
        public static final String OPTIONS          = "OPTIONS";
        public static final String User             = "User";
        public static final String Group            = "Group";
        public static final String INCLUDE          = "Include";
        public static final String ServerAdmin      = "ServerAdmin";
        public static final String ServerName       = "ServerName";
        public static final String DocumentRoot     = "DocumentRoot";
        public static final String Directory        = "<Directory";
        public static final String AddType          =  "AddType application";
        public static final String AddTypeJNLP      =  "AddType application/x-java-jnlp-file JNLP";
        

        public static final String ScriptAlias      = "ScriptAlias";
        public static final String PIDFILE          = "PIDFILE";
        public static final String HTTPD            = "HTTPD";
        public static final String pidfile          = "pidfile";
        public static final String PidFile          = "PidFile";
        public static final String httpd            = "httpd";
        public static final String ServerRoot       = "ServerRoot";
        public static final String lockfile         = "lockfile";

        public static final String ScoreBoardFile   =  " ScoreBoardFile";
        public static final String Listen           = "Listen";
        public static final String Port             = "Port";
        public static final String CONFFILE         = "CONFFILE";
        public static final String ErrorLog         = "ErrorLog";
        public static final String CustomLog        = "CustomLog";

        public static final String CONF_FILE         = "CONF_FILE";

        // utility variable Strings
        public static final String DOCUMENROOTDIRFIRST = "dummy name";

        public static String PREVIOUSKEY            = null;



        public static boolean modifyLinuxStartStopFile(Installer installer){
            String string       =   IO.readFileToString(installer.getVirginStartStopFile());
            StringBuilder sb    =   new StringBuilder();
            Scanner scanner     =   new Scanner(string);
            PREVIOUSKEY         =   null;

            String line         =   null;
            String trimmedline  =   null;
            String newLine      =   null;


            /* OPTION line can be present or not.
             * Therefore, just incase it will be writtedm after "lockfile" line
             * Must look like this: OPTIONS="-f /etc/httpd/conf/httpd8080.conf"
             * Just
             */
            String filepath     =   installer.getDestinationConfFileAbsolutePath();
            String options      =   OPTIONS + "="+insertQuotes("-f "+filepath);

            while (scanner.hasNextLine()){
                line            =   scanner.nextLine();
                trimmedline     =   line.trim();
                newLine         =   null;

                if (trimmedline.startsWith(OPTIONS)){
                    // must look like this: OPTIONS="-f /etc/httpd/conf/httpd8080.conf"
                    newLine             =   options;
                }
                else if(trimmedline.startsWith(httpd )){
                    /* must look like this: httpd=${HTTPD-/usr/sbin/httpd} */
                    String httpdpath        =   installer.getHttpdCommandPath();
                    newLine                 =   httpd +"=${HTTPD-"+httpdpath+"}";
                }
                else if (trimmedline.startsWith(pidfile) || trimmedline.startsWith(PIDFILE)){
                    // must look like this: pidfile=${PIDFILE-/var/run/httpd.pid}
                    String pidfilepath      =   installer.getPidFileAbsolutePath();
                    newLine                 =   pidfile +"=${PIDFILE-"+pidfilepath+"}";
                }
                else if (trimmedline.startsWith(CONFFILE)){
                    // must look like this: CONFFILE=/etc/httpd/conf/httpd8080.conf
                    String confpath         =   installer.getDestinationConfFileAbsolutePath();
                    newLine                 =   "	"+ CONFFILE+"="+confpath;
                }
                 else if (trimmedline.startsWith(lockfile)){
                    // must look like this: lockfile=${LOCKFILE-/var/lock/subsys/httpd8080}
                    newLine                 =  line.replaceAll(httpd, httpd+installer.getPort());
                    newLine                +=  "\n";
                    newLine                +=  options;
                }



                if (newLine != null){
                    // keep modify tag
                    sb.append(MODIFYTAG );
                    sb.append("\n");

                    sb.append(newLine);
                    sb.append("\n");
                }
                else {
                    sb.append(line);
                    sb.append("\n");
                }
                   

             }
                    
            File dst      = new File (installer.getInstallationStartStopFileAbsolutePath());
            boolean write = IO.writeFileFromString(sb.toString(),dst);
            return write;
        }
        public static boolean modifyLinuxConfigFile(Installer installer){
            File   src                  =   installer.getVirginConfFile();
            String string               =   IO.readFileToString(src );
            StringBuilder sb            =   new StringBuilder();
            Scanner scanner             =   new Scanner(string);
            Scanner linescanner         =   null;
            PREVIOUSKEY                 =   null;

            String line                 =   null;
            String newLine              =   null;
            String key                  =   null;
            boolean isAddTypeJNLPWriiten =   false;

            while (scanner.hasNextLine()){
               
                line            =   scanner.nextLine();
                linescanner     =   new Scanner(line);
                newLine         =   null;

                if (linescanner.hasNext()){
                    key         =   linescanner.next();

                }
                else{   
                    sb.append(line);
                    sb.append("\n");
                    linescanner.close();
                    continue  ;
                }



                if (key.equalsIgnoreCase(User )){
                 /*   User bayes
                      Group apache
                 */
                    scanner.nextLine();// skip Group line

                    newLine              =   User    + " "+ Installer.getUserName()  + "\n";
                    newLine             +=   Group   + " "+ installer.group   ;
                    PREVIOUSKEY          =   User;
                }


                else if (key.equalsIgnoreCase(PidFile )){
                    // must look like : PidFile run/httpd.pid
                    newLine              =   PidFile    + " "+ installer.getPidFileAbsolutePath();
                    PREVIOUSKEY          =   PidFile ;
                }

                else if (key.equalsIgnoreCase(ServerAdmin)){
                    // must look like : ServerAdmin admin@your-domain.com

                    newLine              =   ServerAdmin   + " "+ installer.email;
                    PREVIOUSKEY          =   ServerAdmin;
                }

                else if (key.equalsIgnoreCase(ServerName) ||key.equalsIgnoreCase("#"+ServerName) ){
                    // must look like : ServerName 1.2.3.4

                    newLine              =   ServerName   + " "+ installer.getServerIP() + ":"+installer.getPort(); 
                    PREVIOUSKEY          =   ServerName;
                }
                else if (key.equalsIgnoreCase(PidFile )){
                    // must look like : PidFile run/httpd.pid8080

                    newLine                 =   PidFile    + " "+ installer.getPidFileAbsolutePath();
                    PREVIOUSKEY             =   PidFile ;
                }
                else if (key.equalsIgnoreCase( DocumentRoot )){
                     // must look like :DocumentRoot "/var/www/html"

                    String droot         =   insertQuotes(installer.getDocumentRootAbsolutePath());
                    newLine              =   DocumentRoot   + " "+ droot;
                    PREVIOUSKEY          =   DocumentRoot;

                }
                else if (key.equalsIgnoreCase(  ScriptAlias )){
                     // must look like :ScriptAlias /cgi-bin/ "/home/group/bayes_account/cgi-bin/"

                    String path          =    FileManager. getCgiBinDir()+"/";
                    newLine              =    ScriptAlias   +" /cgi-bin/ "+insertQuotes(path);
                    PREVIOUSKEY          =    ScriptAlias;

                }
                else if (key.equalsIgnoreCase(  Listen )){
                    // must look like : Listen 8080
                    newLine              =     Listen   +" "+ installer.getPort();
                    PREVIOUSKEY          =     Listen;

                }

                else if (key.equalsIgnoreCase(  ErrorLog  )){
                    // must look like : ErrorLog logs/error_log8080
                    String cstLog        =     installer.getErrorLogAbsolutePath();
                    newLine              =     ErrorLog   +" " + cstLog ;
                    PREVIOUSKEY          =     ErrorLog ;

                }

                else if (key.equalsIgnoreCase(  CustomLog  )){
                    // must look like : CustomLog logs/access_log8080 common
                    String cstLog        =     installer.getCustomLogAbsolutePath();
                    newLine              =     CustomLog   +" " + cstLog + " "+"common";
                    PREVIOUSKEY          =     CustomLog ;

                }
               else if (line.contains( AddType ) ){
                    // must look like : AddType application.......
                   
                    if (isAddTypeJNLPWriiten){
                        if (line.contains(AddTypeJNLP)){
                            /* if we already have written Add Type Jnlp
                             * don't keep same line again
                             */
                             newLine              =      line + "\n";
                        }
                        else{/*do nothing*/}
                    }
                    else{
                        newLine                 =     line + "\n";
                        newLine                 =     newLine + AddTypeJNLP;
                        isAddTypeJNLPWriiten    =     true;
                    }
                    
                    PREVIOUSKEY          =     key ;
               }



                 else if (key.equalsIgnoreCase( INCLUDE  )){
                    // uncomment include line
                    newLine              =     "#"+line ;
                    PREVIOUSKEY          =      INCLUDE  ;

                }


               else if (key.equalsIgnoreCase( Directory )){
                    if (PREVIOUSKEY.equalsIgnoreCase( DocumentRoot)){
                        //skip first DIRECTORY FIELD after DocumentRoot
                        PREVIOUSKEY     =    DOCUMENROOTDIRFIRST;
                     }

                    else if (PREVIOUSKEY.equalsIgnoreCase( DOCUMENROOTDIRFIRST)){
                         //overwrite second DIRECTORY FIELD after DocumentRoot
                        // Must be of following form:  <Directory "/var/www/htdocs8080">
                        String str      =   " "+ insertQuotes(installer.getDocumentRootAbsolutePath())+ ">";
                        newLine         =    Directory  + str;
                        PREVIOUSKEY     =    "";
                     }
                    


                    else if (PREVIOUSKEY.equalsIgnoreCase( ScriptAlias)){

                        // Must be of following form:  <Directory "/var/www/htdocs8080">
                        String str      =   " "+ insertQuotes(FileManager.getCgiBinDir().getAbsolutePath())+ ">";
                        newLine         =   Directory  + str;


                        scanner.nextLine();//   skip line "  AllowOverride None"
                        newLine         =   newLine + "\n";
                        newLine         =   newLine + "    AllowOverride AuthConfig";



                        PREVIOUSKEY     =   "";

                    }
                }
  
                if (newLine != null){
                // keep modify tag
                sb.append(MODIFYTAG );
                sb.append("\n");

                sb.append(newLine);
                sb.append("\n");
                }


                else {
                    sb.append(line);
                    sb.append("\n");
                }

            }
            File dst      = new File (installer.getInstallationConfFileAbsolutePath());
            boolean write = IO.writeFileFromString(sb.toString(),dst);

            linescanner.close();
            scanner.close();
            return write;
        }



        public static boolean modifySunStartStopFile(Installer installer){
            String string       =   IO.readFileToString(installer.getVirginStartStopFile());
            StringBuilder sb    =   new StringBuilder();
            Scanner scanner     =   new Scanner(string);
            PREVIOUSKEY         =   null;


            String line         =   null;
            String newLine      =   null;

            while (scanner.hasNextLine()){
                line            =   scanner.nextLine().trim();
                newLine         =   null;



               if (line.startsWith(HTTPD)){
                    // must look like this: HTTPD="/usr/local/apache/bin/httpd  -f /usr/local/apache/conf/httpd8080.conf"
                    String httpdpath        =   installer.getHttpdCommandPath();
                    String configFilepath   =   installer.getDestinationConfFileAbsolutePath();
                    String command          =   httpdpath + " -f "+ configFilepath;
                    newLine                 =   HTTPD + "="+insertQuotes(command);
               }
               else if (line.startsWith(PIDFILE) ||line.startsWith(pidfile)){
                    // must look like this: PIDFILE=/usr/local/apache/logs/httpd8080.pid
                    String pidfilepath      =   installer.getPidFileAbsolutePath();
                    newLine                 =   PIDFILE + "="+pidfilepath;
               }

                if (newLine != null){
                    // keep modify tag
                    sb.append(MODIFYTAG );
                    sb.append("\n");

                    sb.append(newLine);
                    sb.append("\n");
                }
                else {
                    sb.append(line);
                    sb.append("\n");
                }


             }
          
            File dst      = new File (installer.getInstallationStartStopFileAbsolutePath());
            boolean write = IO.writeFileFromString(sb.toString(),dst);
            return write;
        }
        public static boolean modifySunConfigFile(Installer installer){
            File   src          =   installer.getVirginConfFile();
            String string       =   IO.readFileToString(src );
            StringBuilder sb    =   new StringBuilder();
            Scanner scanner     =   new Scanner(string);
            Scanner linescanner =   null;
            PREVIOUSKEY         =   null;


            String line         =   null;
            String newLine      =   null;
            String key          =   null;


            while (scanner.hasNextLine()){
                line            =   scanner.nextLine().trim();
                linescanner     =   new Scanner(line);
                newLine         =   null;

                if (linescanner.hasNext()){
                    key         =   linescanner.next();

                }
                else{
                    sb.append(line);
                    sb.append("\n");
                    linescanner.close();
                    continue  ;
                }


                 if (key.equalsIgnoreCase(User )){
                    /*
                     User bayes
                     Group apache
                     */

                    scanner.nextLine();// skip Group line

                    newLine              =   User    + " "+ Installer.getUserName()  + "\n";
                    newLine              +=   Group   + " "+ installer.group   ;
                    PREVIOUSKEY          =   User;
                    }
                   else if (key.equalsIgnoreCase(ServerRoot)){
                    // must look like : ServerRoot "/usr/apache"

                    String sr               =   installer.getServeroot();
                    newLine                 =   ServerRoot    + " "+ insertQuotes(sr);
                    PREVIOUSKEY             =   ServerRoot ;
                }

                 else if (key.equalsIgnoreCase(PidFile )){
                    // must look like : PidFile /var/run/httpd.pid

                    newLine                 =   PidFile    + " "+ installer.getPidFileAbsolutePath();
                    PREVIOUSKEY             =   PidFile ;
                }

                else if (key.equalsIgnoreCase("#"+Listen )){
                    /*must look like :
                            #Listen 3000
                            #Listen 12.34.56.78:80
                            Listen 8080
                     */
                    newLine                 =   line +  "\n";
                    line                    =   scanner.nextLine().trim();

                   while (line.startsWith("#"+Listen)){
                        newLine                 +=   line +  "\n";
                        line                    =   scanner.nextLine().trim();

                    }

                    newLine                 +=   Listen +  " "+installer.getPort();
                    newLine                 +=  "\n";   // insert extra blank line
                    PREVIOUSKEY             =   Listen  ;
                }



                else if (key.equalsIgnoreCase(ServerAdmin)){
                     // must look like : ServerAdmin admin@your-domain.com

                    newLine                 =   ServerAdmin   + " "+ installer.email;
                    PREVIOUSKEY             =   ServerAdmin;
                }

                else if (key.equalsIgnoreCase(ServerName)||key.equalsIgnoreCase("#"+ServerName)){
                    // must look like : ServerName 1.2.3.4

                    newLine                 =   ServerName   + " "+ installer.getServerIP()+  ":"+installer.getPort();;
                    PREVIOUSKEY             =   ServerName;
                }

                else if (key.equalsIgnoreCase( DocumentRoot )){
                     // must look like : DocumentRoot "/usr/local/apache/htdocs8080"

                    String droot            =   insertQuotes(installer.getDocumentRootAbsolutePath());
                    newLine                 =   DocumentRoot   + " "+ droot;
                    PREVIOUSKEY             =   DocumentRoot;

                }
                else if (key.equalsIgnoreCase(  ScriptAlias )){
                     // must look like : ScriptAlias /cgi-bin/ "/home/bayes_account/cgi-bin/"

                    String path             =    FileManager. getCgiBinDir()+"/";
                    newLine                 =    ScriptAlias   +" /cgi-bin/ "+insertQuotes(path);
                    PREVIOUSKEY             =    ScriptAlias;

                }
                else if (key.equalsIgnoreCase(  Port)){
                     // must look like : Port 8080

                    newLine                 =     Port   +" "+ installer.getPort();
                    PREVIOUSKEY             =     Port;

                }


             // ScoreBoardFile

              else if (key.equalsIgnoreCase(  ScoreBoardFile  )){
                    // must look like : ScoreBoardFile logs/httpd8080.scoreboard
                    String portStr          =     ""+installer.getPort();
                    String httpdstr         =     "httpd";
                    newLine                 =     line.replace(httpdstr,httpdstr +portStr) ;
                    PREVIOUSKEY             =     ScoreBoardFile ;

                }

               else if (key.equalsIgnoreCase(  ErrorLog  )){
                    // must look like : ErrorLog /var/apache/logs/error_log
                    String portStr          =     ""+installer.getPort();
                    String cstLog           =     installer.getErrorLogAbsolutePath();
                    newLine                 =     ErrorLog   +" " + cstLog ;
                    PREVIOUSKEY             =     ErrorLog ;
                }


                else if (key.equalsIgnoreCase(  CustomLog  )){
                    // must look like : CustomLog /var/apache/logs/access_log8080 common
                    String cstLog        =     installer.getCustomLogAbsolutePath();
                    newLine              =     CustomLog   +" " + cstLog + " "+"common";
                    PREVIOUSKEY          =     CustomLog ;
                }
                else if (line.contains( AddType ) ){
                    // must look like : AddType application.......
                    newLine              =     line + "\n";
                    newLine              =     newLine + AddTypeJNLP;
                    PREVIOUSKEY          =     key ;
               }

                else if (line.startsWith( Directory )){
                     if (PREVIOUSKEY.equalsIgnoreCase( DocumentRoot)){
                        //skip first DIRECTORY FIELD after DocumentRoot
                        PREVIOUSKEY     =    DOCUMENROOTDIRFIRST;
                     }


                     else if (PREVIOUSKEY.equalsIgnoreCase( DOCUMENROOTDIRFIRST)){

                        // Must be of following form:  <Directory "/var/www/htdocs8080">
                        String str          =   " "+ insertQuotes(installer.getDocumentRootAbsolutePath())+ ">";
                        newLine             =    Directory  + str;
                        PREVIOUSKEY         =    "" ;
                     }

                    else if (PREVIOUSKEY.equalsIgnoreCase( ScriptAlias)){

                        // Must be of following form:  <Directory "/var/www/htdocs8080">
                        String str          =   " "+ insertQuotes(FileManager.getCgiBinDir().getAbsolutePath())+ ">";
                        newLine             =   Directory  + str;
                        PREVIOUSKEY         =    "" ;
                    }

                }

                if (newLine != null){
                // keep modify tag
                sb.append(MODIFYTAG );
                sb.append("\n");

                sb.append(newLine);
                sb.append("\n");
                }
                else {
                    sb.append(line);
                    sb.append("\n");
                }


            }
            File dst      = new File (installer.getInstallationConfFileAbsolutePath());
            boolean write = IO.writeFileFromString(sb.toString(),dst);


            linescanner.close();
            scanner.close();
            return write;
        }




   






        public static String insertQuotes(String in){
            String out = "\""+in + "\"";
            return out;
        }
}
