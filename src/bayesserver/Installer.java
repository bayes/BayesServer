/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import Compilers.IntelCompiler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.Properties;
import utilities.*;
import java.util.regex.*;

public class Installer implements Constants{

     
    public static  int DEFAULT_PORT                         =   8080;
    public static final String GET_SCRIPTS_SCRIPT           =   "Bayes_get_scripts";
    public static final String GET_INFO_SCRIPT              =   "Bayes_get_info";
    public static final String LIB_LIST_RESOLVED_SCRIPT     =   "Bayes_lib_list_Resolved";
    public static final String LIB_LIST__UNRESOLVED_SCRIPT  =   "Bayes_lib_list_UnResolved";
    public static final String [] LOCAL_SCRIPTS             =    {  GET_SCRIPTS_SCRIPT,
                                                                    GET_INFO_SCRIPT,
                                                                    LIB_LIST_RESOLVED_SCRIPT,
                                                                    LIB_LIST__UNRESOLVED_SCRIPT
                                                                 };

    private PLATFORM platform             =   PLATFORM.SUN;
    
    public  String installDir             =   null;
    private String serverIP               =   null;
    public  String serverHostName         =   null;
    private  String serverQueue           =   Constants.DEFAULT_QUEUE;
    public  String errorMessage           =   "";
    public  String installationKitName    =   null;
    
    public  String group                  =   null;
    public  String email                  =   "";
    private  String shell                 =   "";
    private  String unixenv                 =   "";
    
    
    
    private int port                      =   8080;
    private  String []groupChoices        =   null;
    public String userHome                =   null;
    public String updateMessage           =   "";
  

  

    private  String destinationDirForConfFile  =   "";
    private  String destimationConfFileName =  "/httpd"+getPort() + ".conf";
    
    
    private  String destinationDirForStartStopFile  =   "";
    private  String destinationStartStopFilename    =   "";
    
    private final String DOCROOT_DIRECTORY_KEY  =   "DOCUMENT ROOT DIRECTORY";
    private  String documentRootParenDir        =   "";
    private  String documentRoot                =   "";
    
    private final String PID_DIRECTORY_KEY      =   "PID DIR";
    private String pidFileDir                   =   "";
    private String pidFileName                  =   "/httpd"+getPort() + ".pid";
    
    private final String LOGS_DIRECTORY_KEY   =   "LOGS DIR";
    private  String logsDir                 =   "";
    
    private final String SEREVR_ROOT_KEY    =   "SEREVR ROOT DIR";
    private  String serveroot               =   "";

    private final String APACHE2_DIR_KEY    =   "APACHE2 DIR";
    private  Apache2 apache2                =   new Apache2();
    
    private final String HTTPD_PATH_KEY    =   "HTTPD PATH";
    private String httpdCommandPath        =   "";
    
    private  File virginStartStopFile       =   null;
    private  File virginConfFile            =   null;
    
    private  String errorLog                =   "";
    private  String customLog               =   "";


    public  String osarch                   =   null;
    public  String osname                   =   null;
    public  String osrelease                =   null;

    
    private String fortanCompilerName       =   NOCOMPILER;
    private String cCompilerName            =   NOCOMPILER;
    private String fortanConfigSrcipt       =   NOCOMPILER;
    private String cConfigSrcipt            =   NOCOMPILER;
    private String fortanDir                =   NOCOMPILER;
    private String cDir                     =   NOCOMPILER;


    private boolean OSValid                 =   false;
    private boolean OSArchValid             =   false;
    private boolean OS64Bit                 =   true;

    private boolean passwordProtected       =   false;
    private boolean emailSubscribe          =   false;
    
    public String installationErrorMessage  =   null;
   
    private InstallationInfo installInfo    =   new  InstallationInfo();
    private String    installMessage        =   "Install Bayesian Analysis Software";
    private String    serverSoftware        =    URLManager.getInstallationVersion();
    private String    progressMessage       =   "";
    
    private boolean skipApacheSetup           =   false;
    private Properties properties             =   new Properties();
    /* initialization block */
    {
        setPortAndUpdateDependencies(DEFAULT_PORT);


    }
   

    int counter                         =   1;
    public INSTALLATION installation    =   INSTALLATION.START;

    public void updateInstallationState(INSTALLATION newInstallStage){
        installation = newInstallStage;
    }

    public Installer(){
        System.out.println("Starting installer");
        System.out.println("Reading System Variables");
        recordSystemVariables();

        System.out.println("Reading Network Variables");
        recordNetworkVariables ();

        System.out.println("Reading Compiler Variables from previous installation");
        recordCompilerVariables ();
        
        System.out.println("Reading Apache Sererv Variable");
        recordApacheVariables();
        
        System.out.println("Reading Properties File"); 
        readFromInstallationProperties();

        System.out.println("Reading Intsallation Info File");
        loadInstallationInfo ();
        checkForupdates ();
        
    }

    public void      checkForupdates (){
       String clientInstVer                 =   getInstallInfo().getInstallationInterfaceVersion();
       String serverInstVer                 =   getServerSoftware();
       String  message                      =   "";


       boolean instalInfoNotFound           =   !getInstallInfo().isLoaded();
       boolean isNotValidVersion            =   ( clientInstVer == null || serverInstVer.length()<1)? true: false;


       boolean newInstlIsAvailable          =  !clientInstVer.equals(serverInstVer );

       if (instalInfoNotFound  || isNotValidVersion || newInstlIsAvailable){
        message                  =      String.format(
                                        "<html>"+
                                        "Bayesian Analysis Of Common NMR Problems<br>" +
                                        "version %s is being installed "+
                                        "</html>",
                                         serverInstVer);
       }
       else {
           message                  =      String.format(
                                        "<html>"+
                                        "Your software is up to date (version %s).<br>" +
                                        "</html>",
                                         serverInstVer);

       }
       this.setInstallMessage(message);

   }
    public boolean   isReadyToInstall(){
        String message;
        System.out.println("Skip apache checks? "+ this.skipApacheSetup);
       // is valid platform
       if (this.isOSValid() == false){
          message = String.format("System OS is invalid\n"+
                                  "Do you want to proceed "
                                  + "with installation?" );
          boolean proceed = utilities.DisplayText.popupDialog(message);
          if ( proceed  == false){
            return false;
          }
       }

       // is valid hardware
       if (this.isOSArchValid() == false){
          message = String.format("OS release is invalid\n"+
                                   "Exit installation." );
          utilities.DisplayText.popupMessage(message );
          return false;
       }

       //is valid users
       boolean validUser                =   isValidUser();
       if ( validUser == false){
           String username              =   getUserName();
           String msg                   =   String.format(
                   "User %s is not legal. Exit installation.",
                   username);
           utilities.DisplayText.popupMessage(msg);
            return false;
       }

       // is user group  set  specified
       if (group == null || group.length()< 1){
        utilities.DisplayText.popupMessage("User group is not set.\n" +
                "Exit installation.");
        return false;
       }

        // is C shell set
       if (this.isCshell() == false){
         message  = String.format(
                "Installation is only allowed within \n"+
                "C-type shells (i.e. csh and tsch).\n"+
                "Current shell appears to be %s.\n"+
                "Extit installaion.",
                this.getShell());
        utilities.DisplayText.popupMessage(message);
        return false;
       }



       // is email set
       String anEmail = this.email;
       if (anEmail == null || anEmail.length()< 1){
        utilities.DisplayText.popupMessage("Email is not set.\n" +
                "Exit installation.");
        return false;
       }
       boolean isFullEmail = isEmailValid(anEmail);
       if (isFullEmail == false){

        String msg    = String.format(
                "Email \"%s\" appear to be invalid.\n"+
                "Please check email tool-tip for information\n"
                +"regarding valid email format."
                , anEmail);
        utilities.DisplayText.popupMessage(msg );

        return false;
       }


       // is queue set
       String aQueue        =    getServerQueue();
       if (aQueue == null || aQueue.isEmpty()){
          String msg    =   String .format(
                  "Server queue is not set.\n" +
                  "Do you want to proceed\n"+
                  "installation with the queue\n" +
                  "set to defautlt value \"%s\")?",
                  DEFAULT_QUEUE
                  );
        boolean proceed = utilities.DisplayText.popupDialog(msg);
        if (proceed == false){ return false;}
        else{
            setServerQueue(DEFAULT_QUEUE);
        }
       }
       else{
            boolean isValidQueue = isQueueValid(aQueue);
            if (isValidQueue == false){

            String msg    = String.format(
                "Server queue \"%s\" appear to be invalid.\n"+
                "Quene must have no white spaces.\n"+
                "Exiting installation."
                , aQueue);
            utilities.DisplayText.popupMessage(msg );

            return false;
           }
       }

              // is Fortran Compiler setup script is specified
       if (getFortanCompilerName() == null || getFortanCompilerName().isEmpty()){
        utilities.DisplayText.popupMessage("Fortan compiler setup script is not sepcified.\n" +
                "Abort installation.");
        return false;
       }



       // is C Compiler setup script is specified
       if (getcCompilerName() == null || getcCompilerName().isEmpty()){
        utilities.DisplayText.popupMessage("C compiler setup script is not sepcified.\n" +
                "Abort installation.");
        return false;
       }

   if (this.skipApacheSetup == false){
        boolean apacheReady = isReadyToInstallApache();
        if (apacheReady == false){
            utilities.DisplayText.popupMessage("Apache server is not correctly configured.\n" +
                "Abort installation.");
            return false;}

       } // end checking apache servers




       return true;
   }
    public boolean   isReadyToInstallApache(){
        String message;
        if (this.isUbuntu()){
                return this.apache2.isValid();
        }
        else{
           
           // is configuration file found/set
           if (getVirginConfFileName().isEmpty()){
            utilities.DisplayText.popupMessage("Configuration file is not set\n" +
                    "Abort installation.");
            return false;
           }

            // is start/stop file found
           if (getVirginStartStopFileName().isEmpty()){
            utilities.DisplayText.popupMessage("Start/Stop file is not set.\n" +
                    "Abort installation.");
            return false;
           }

            // is httpd command dir location specified
            if (getHttpdCommandPath() == null || getHttpdCommandPath().isEmpty()){
                utilities.DisplayText.popupMessage( "The path to HTTPD command file\n" +
                                                "is  not specified.\n"+
                                                 "Abort installation.");
                return false;
             }

       // is pidfile specified
         if (getPidFileDirPath()  == null || getPidFileDirPath().isEmpty()){
            utilities.DisplayText.popupMessage("PIDFILE was not set.\n" +
                    "Abort installation.");
            return false;
         }
         
            // document root directory check
            String docRootDir        =   this.getDocumentRoot();
            if (docRootDir    == null || docRootDir.isEmpty()){
                utilities.DisplayText.popupMessage("Document Root directory was not set.\n" +
                        "Abort installation.");
                return false;
           }

            // sererv root directory check
            String serevrRootDir        =   this.getServeroot();
            if (serevrRootDir    == null || serevrRootDir.isEmpty()){
                utilities.DisplayText.popupMessage("Server Root directory was not set.\n" +
                        "Abort installation.");
                return false;
           }
         
         // sererv root directory check
            String logDir        =   this.getLogsDir();
            if (logDir    == null || logDir.isEmpty()){
                utilities.DisplayText.popupMessage("Logs directory was not set.\n" +
                        "Abort installation.");
                return false;
           }
        

       } 


       return true;
   }
    
    public void         install(){

        setProgressMessage("Start installation");
        updateInstallationState (INSTALLATION.START);
        installationErrorMessage            =   null;
        String errormessage                 =    null;


        try{
               installationKitName =  URLManager.getInstallationKitName(platform, is64Bit());
                if (installationKitName  == null){
                     errormessage     =    "Failed to get installation kit name";
                     setProgressMessage( errormessage);
                     throw new Exception (errormessage );
                }
                setProgressMessage("Installation kit name  = "+ installationKitName);
                updateInstallationState (INSTALLATION.DOWNLOAD);
            System.out.println("START DOWNLOADING INSTALLATION PACKAGE "+ installationKitName);
                setProgressMessage("Start downloading "+installationKitName);
                boolean success = download();

                if (success == false){
                     errormessage     =    "Failed to download installation kit";
                     setProgressMessage( errormessage);
                     throw new Exception (errormessage );
                }



                setProgressMessage("Change File Permissions");
                // make sure we clean files from previous installation
               System.out.println("CLEAN INSTALLATION DIRECTORY");
                cleanInstalationDirOnInstallationStart();



                 setProgressMessage("Start untarring downloaded kits");
                 updateInstallationState (INSTALLATION.UNARCHIVE);
                 // calling  cleanInstalationDirOnInstallationStart() made
                 // all directories writable
                 // unzip, untar, delete *.gz and *.tar files
                 unarchive();



                  // trmporarily make all directories writable
                 setAllDirWritable(true);

                 updateInstallationState (INSTALLATION.INSTALL);
      
       if (skipApacheSetup == false){ 
                if (isUbuntu()){
                     setProgressMessage("Write apache2 configuration files");
                    this.setupApache2();
                    boolean apache2IsGood= this.apache2.writeConfiguratioFiles ();
                    if (apache2IsGood == false){
                         errormessage     =    apache2.getConfigFileWriteError();
                         setProgressMessage( errormessage);
                         throw new Exception (errormessage );
                    }
                }
                else{
                     setProgressMessage("Write Start/Stop file");
                    // write Start/Stop file
                     boolean isStartStop  = writeStartStopFiles();
                     if (isStartStop   == false){
                         errormessage     =    "Failed to write Start/Stop file";
                         setProgressMessage( errormessage);
                         throw new Exception (errormessage );
                     }
                     setProgressMessage("Write configuration file");
                     // write Configuration file
                     boolean isConfig  = writeCentOsApacheConfiguration();
                     if ( isConfig== false){
                         errormessage     =    "Failed to write configuration file";
                         setProgressMessage( errormessage);
                         throw new Exception (errormessage );
                     }
                
                }
                
        }


                setProgressMessage("Write HTAccess file");
                // write htacees files
                boolean isHTAccessFileCreated = writeHTAccessFile();
                if (isHTAccessFileCreated  == false){
                     errormessage     =    "Failed to write HTAccess file";
                     setProgressMessage( errormessage);
                     throw new Exception (errormessage );
                }

                setProgressMessage("Create empty .cshrs file");
                // make sure that empty .cshrs file exists
                boolean istCSHRCFileCreated = writeCSHRCFile();
                if (istCSHRCFileCreated  == false){
                    errormessage     =    "Faield to create .cshrs file";
                    setProgressMessage( errormessage);
                    throw new Exception (errormessage );
                }

                setProgressMessage("Write login file");
                // write .login file
                boolean isLoginFileCreated =writeLoginFile();
                if (isLoginFileCreated  == false){
                    errormessage     =    "Failed to write .login file";
                    setProgressMessage( errormessage);
                    throw new Exception (errormessage );
                }


                setProgressMessage("Write FotranSetup file");
                // write FortanSetup dir
                boolean isFortranSetupFileCreated = writeFortranSetupFile();
                if (isFortranSetupFileCreated  == false){
                    errormessage     =    "Failed to write FotranSetup  file";
                    setProgressMessage( errormessage);
                    throw new Exception (errormessage );
                }

                setProgressMessage("Write C compiler option file");
                // write FortanSetup dir
                boolean isCCompileFileCreated = writeCCompileFile();
                if (isCCompileFileCreated   == false){
                    errormessage     =    "Failed to write C compiler option file";
                    setProgressMessage( errormessage);
                    throw new Exception (errormessage );
                }

                setProgressMessage("Write Fostran compiler option file");
                // write FortanSetup dir
                boolean isFCompileFileCreated = writeFCompileFile();
                if ( isFCompileFileCreated  == false){
                    errormessage     =    "Failed to write Fortran compiler option file";
                    setProgressMessage( errormessage);
                    throw new Exception (errormessage );
                }




                setProgressMessage("Write launch.jnlp file");
                // modify launch.jnlp file in Bayes directory
                boolean isJNLPModified = writeJNLPFile();
                if (isJNLPModified  == false){
                    errormessage     =    "Failed to write launch.jnlp file";
                    setProgressMessage( errormessage);
                    throw new Exception (errormessage );
                }
           if (skipApacheSetup == false && isUbuntu() == false){
               setProgressMessage("Write  Start/Stop file executable");
                // make sure Start/Stop file is executable
                makeStartStopFileExcecutable();
           }

                setProgressMessage("Write installation info file");
                //write installation information dir at the end
                writeInstallationFile();

               //Store properties to persistent storage
                updateAndStoreInstallationProperties();
               
                
                setProgressMessage("Reset Permissions");
                // set permission to writable for all directories
                setAllDirWritable(false);

                
                
                setProgressMessage("Installation is complete");
                updateInstallationState (INSTALLATION.COMPLETE);
                
                writeDebugFile("Installation is compete.");
                
                // register Installation
                registerInstallation();
                
                writeDebugFile("Registration is compete.");

        }
        catch(Exception e){
            String err          =   e.getMessage();
            installationErrorMessage = "Unknown";
             updateInstallationState (INSTALLATION.FAILED);
            if (err != null && err.isEmpty() == false){
                installationErrorMessage = err;
            }
            e.printStackTrace();

        }
       


       
 
    }
    private  void  cleanInstalationDirOnInstallationStart(){
        System.out.println("Cleaning files from previous installation");
        File bin        =   FileManager.getBinDir();
        File sbin       =   FileManager.getSBinDir();
        File lib        =   FileManager.getLibDir();
        File system     =   FileManager.getSystemDir();
        File cgibin     =   FileManager.getCgiBinDir();


        File [] dirs    =   {bin, sbin, lib};

        // set permission to writable for all directories
        setAllDirWritable(true);


        for (File dir : dirs) {
            if (dir.exists() && dir.isDirectory()){
                IO.deleteDirectory(dir);
            }
        }


        
        //  In System directory files "users", "timings",
        //  and files with previous installation setting
        //   must NOT BE DELETED.

         if (system .exists() && system .isDirectory()){
              File [] allfiles = system.listFiles();
              for (File file :  allfiles) {
                 if( file.getName().contains("httpd")){
                      file.delete();
                 }
                

             }
         }

         if (cgibin.exists() && cgibin.isDirectory()){
                IO.deleteDirectory(cgibin);
         }

    
        

    }
    public boolean download(){

        try {
            File dist           =   getLocalInstallationGZipFile();
            boolean isCreated   =   dist.createNewFile();
            String src          =   URLManager. getInstallationKitURL(installationKitName);


           Downloader.downloadFile(src,dist);
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
            errorMessage = String.format("Failed to download installation kit.\n" +
                    "Error message:\n%s", ex.getMessage());
            DisplayText.popupMessage(errorMessage);

            return false;
        }

        return true;
    }
    public boolean unarchive(){

        File gzipFile =  getLocalInstallationGZipFile();
        File tarFile  =  getLocalInstallationTarFile();
        FileCompress.unGzip(gzipFile.getAbsolutePath(), tarFile.getAbsolutePath() );

        String []command      = {"tar", "-xf", tarFile.getPath() } ;
        String out            =  RunCommand.execute(command );

        System.out.println("Unarchiving message = "+ out);
        //clean up
        gzipFile.delete();
        tarFile.delete();

       return true;

    }
    public boolean writeHTAccessFile(){
        File htAccessFile           =   FileManager.getHTAccessFile();
        File hiddenHtAccessFile     =   FileManager.getHiddenHTAccessFile();
        File  usersFile             =   FileManager.getUsersFile();

        if (htAccessFile.exists())          {htAccessFile.delete();}
        if (hiddenHtAccessFile.exists())    {hiddenHtAccessFile.delete();}

        String htaccessFileContent  =   HTAccessFile.createContent(usersFile);
        File accessFile             =   (isPasswordProtected())?hiddenHtAccessFile:htAccessFile ;

        boolean isSuccess           = IO.writeFileFromString(htaccessFileContent, accessFile);
        if ( isSuccess == false ){
            errorMessage = String.format(   "Failed to write htaccess file.\n" +
                                            "%s. Exit.",accessFile.getPath() );
            DisplayText.popupMessage(errorMessage);
            return false;
        }
        return true;
    }
    public boolean writeStartStopFiles(){

        // modify httpd dir
        boolean modify = false;

         switch(getPlatform()){
            case SUN    :     modify = ModifyConfigurationFiles.modifySunStartStopFile(this) ; break;
            case LINUX:     modify = ModifyConfigurationFiles.modifyLinuxStartStopFile(this) ; break;
        }

        if (modify == false){
            errorMessage = String.format(   "Failed to write Start/Stop file\n" +
                                            "Exit installation" );
            DisplayText.popupMessage(errorMessage);
            return false;
        }

        return true;
    }
    public boolean writeCentOsApacheConfiguration(){

        boolean modify = false;
         switch(getPlatform()){
            case SUN    :       modify      = ModifyConfigurationFiles.modifySunConfigFile(this) ; break;
            case LINUX:       modify      = ModifyConfigurationFiles.modifyLinuxConfigFile(this) ; break;
        }

         // modify httpd dir
        if (modify == false){
            errorMessage = String.format(   "Failed to write configurations file\n" +
                                            "Exit installation" );
            DisplayText.popupMessage(errorMessage);
            return false;
        }

        return true;
    }
    public boolean writeCSHRCFile(){
        File    cshrcFile       =   FileManager.getCSHRCFile();


        // make sure that empty .scrs file exists
        if ( cshrcFile.exists()){return true;}
        try {
            cshrcFile.createNewFile();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            errorMessage = String.format(   "Failed to create .cshr file.\n%s." +
                                            "Exit.",cshrcFile.getPath() );
            DisplayText.popupMessage(errorMessage);
            return false;
        }
        return true;
    }
    public boolean writeLoginFile(){
        File    loginFile       =   FileManager.getLoginFile();
        String  binDir          =   FileManager.getBinDir().getAbsolutePath();
        String  libDir          =   FileManager.getLibDir().getAbsolutePath();
        String  fSetup          =   FileManager.getFortranSetupFile().getAbsolutePath();

        // write .login file
        String out              =   RunCommand.execute(new String []{"which", "javaws"}).trim();
        File file               =   new File (out);
        String javawsDir        =   file.getParent();
        String loginContent     =   WriteLogin.writeContent(
                                                            binDir,
                                                            libDir,
                                                            javawsDir,
                                                            fSetup);
        boolean isSuccess        =   IO.writeFileFromString(loginContent, loginFile );
        if ( isSuccess == false ){
            errorMessage = String.format(   "Failed to write login file.\n" +
                                            "%s.\n" +
                                            "Exit.",loginFile.getPath() );
            DisplayText.popupMessage(errorMessage);
            return false;
        }

        return true;
    }
    public boolean writeFortranSetupFile(){
        File  fsFile         =   FileManager.getFortranSetupFile();

        setWritebale(fsFile, true);

        String fsContent     =   WriteSetupFortran.writeContent(this);



        boolean isSuccess    =   IO.writeFileFromString(fsContent, fsFile );
        if ( isSuccess == false ){
            errorMessage = String.format(   "Failed to write %s file.\n" +
                                            "Exit.",fsFile.getPath() );
            DisplayText.popupMessage(errorMessage);
            return false;
        }
        else {setWritebale(fsFile, false); }

           

        return true;
    }
    public boolean writeCCompileFile(){
        File  file         =   FileManager.getCCompileFile();

        setWritebale(file  , true);

        String content     =  WriteCCompileFile .writeContent(this);


        boolean isSuccess    =   IO.writeFileFromString(content, file );
        if ( isSuccess == false ){
            errorMessage = String.format(   "Failed to write %s file.\n" +
                                            "Exit.",file.getPath() );
            DisplayText.popupMessage(errorMessage);
            return false;
        }
        else {setWritebale(file, false); }

           

        return true;
    }
    public boolean writeFCompileFile(){
        File  file         =   FileManager.getFCompileFile();

        setWritebale(file  , true);

        String content     =  WriteFCompileFile .writeContent(this);


        boolean isSuccess    =   IO.writeFileFromString(content, file );
        if ( isSuccess == false ){
            errorMessage = String.format(   "Failed to write %s file.\n" +
                                            "Exit.",file.getPath() );
            DisplayText.popupMessage(errorMessage);
            return false;
        }
        else {setWritebale(file, false); }



        return true;
    }
    public boolean writeJNLPFile(){
        File  file              =   FileManager.getJnlpFile();
        if (file.exists() == false){
            errorMessage = String.format(   "Failed to overwrite launch.jnlp file.\n" +
                                            "File was not found.\n"+
                                            "Exit installation.",file.getPath() );
            DisplayText.popupErrorMessage(errorMessage);
            return false;
        }
        else{
             String []command   = new String []{"chmod", "u+w", file.getPath()} ;
             RunCommand.execute(command);
        }
       
       
        String content          =   WriteJnlpFile.writeContent(this, file);
        boolean isSuccess       =   IO.writeFileFromString( content, file );
        if ( isSuccess == false ){
            errorMessage = String.format(   "Failed to overwrite launch.jnlp file.\n" +
                                            "Exit installation.",file.getPath() );
            DisplayText.popupErrorMessage(errorMessage);
            return false;
        }


        String []command   = new String []{"chmod", "u-w", file.getPath()} ;
        RunCommand.execute(command);

        return true;
    }
    public boolean writeInstallationFile(){
        File    instFile       =   FileManager.getInstallationInfoFile();
        getInstallInfo().setInstallationInterfaceVersion(getServerSoftware());
        getInstallInfo().setInstallerVersion(BayesServerApp.VESRION);
        getInstallInfo().setEmail(this.email);
        getInstallInfo().setCpu(Runtime.getRuntime().availableProcessors());
        getInstallInfo().setPassword(this.isPasswordProtected());
        getInstallInfo().setFortanCompiler(this.getFortanCompilerAbsolutePath());
        getInstallInfo().setcCompiler(this.getCCompilerAbsolutePath());
        getInstallInfo().setAccount(getUserName());
        getInstallInfo().setPort(this.getPort());
        getInstallInfo().setQueue(this.getServerQueue());
        getInstallInfo().setSubscribeForUpdates(this.isEmailSubscribe());

        try{
            getInstallInfo().writeToFile(instFile);
        }catch(Exception exp){
            return false;
        }

        return true;
    }
    public boolean writeDebugFile(String content){
      File dir                  =   new File("/tmp");
      Date date                 =   new Date();
      String name               =   "installDebug_"+ date+date.getTime();
      File  file                =    new File (dir,name);
       
        boolean isSuccess       =   IO.writeFileFromString( content, file );
        return true;
    }
    
    public void readFromInstallationProperties(){
        readProperties();
        Properties p      =   this.properties;
        
        /*
        File dir            =  getDirectoryFromProperty(p,DOCROOT_DIRECTORY_KEY );
        if (dir != null){ this.documentRootParenDir = dir.getAbsolutePath();}
        
        dir            =  getDirectoryFromProperty(p,PID_DIRECTORY_KEY );
        if (dir != null){ this.pidFileDir = dir.getAbsolutePath();}
        
        dir            =  getDirectoryFromProperty(p,LOGS_DIRECTORY_KEY );
        if (dir != null){ this.logsDir = dir.getAbsolutePath();}
        
        dir            =  getDirectoryFromProperty(p,SEREVR_ROOT_KEY);
        if (dir != null){ this.serveroot= dir.getAbsolutePath();}
        
        File file       =  getFileFromProperty(p,HTTPD_PATH_KEY );
        if (dir != null){ this.httpdCommandPath = file.getAbsolutePath();}
        */
        File dir            =  getDirectoryFromProperty(p,APACHE2_DIR_KEY  );
        if (dir != null){
            Apache2  ap    = new  Apache2(dir);
            if (ap.checkApache2InstanceIntegrity() == true){
                this.apache2 = ap;
            }
        
        }
    }
    public void updateAndStoreInstallationProperties(){
        Properties p      =   this.properties;
        
        /*
        setProperty(p, DOCROOT_DIRECTORY_KEY , documentRootParenDir);
        setProperty(p, PID_DIRECTORY_KEY , pidFileDir );
        setProperty(p, LOGS_DIRECTORY_KEY  , logsDir );
        setProperty(p, SEREVR_ROOT_KEY ,   serveroot);
        setProperty(p, HTTPD_PATH_KEY , httpdCommandPath);
        
         */
        if (apache2.isValid()){
            setProperty(p, APACHE2_DIR_KEY , apache2.getApache2Dir().getAbsolutePath());
        }
        writeProperties();
    
    }
    
    public void setProperty(Properties props, String key, String value){
        
        if (validProperty(value)){
           props.setProperty(key,value);
        }
        else {
            props.remove(key);
        }
    }
    public File getDirectoryFromProperty(Properties props, String key){
        String val  =    props.getProperty(key);
        if (val == null){return null;}
        File dir           =   new File (val);
        if (dir.exists() && dir.isDirectory()){
            return dir;
        }
        else {return null;}
      
    }
      public File getFileFromProperty(Properties props, String key){
        String val  =    props.getProperty(key);
        if (val == null){return null;}
        File file           =   new File (val);
        if (file.exists() && file.isFile()){
            return file;
        }
        else {return null;}
      
    }
    public boolean validProperty(String property){
        if (property!= null && property.isEmpty()== false){
            return true;
        }
        else {
            return false;
        }
    }
    public void readProperties(){
      FileInputStream in            =   null;
      try{
       File file                    =    FileManager.getInstallationPropertiesFile();
    
       if (file!= null && file.exists()){
            in                      =   new FileInputStream(file);
            this.properties.load(in);
       }
       if (in!=null){ in.close();}
      }
      catch (Exception e){e.printStackTrace();}
      finally{
      }

   }
    public void writeProperties(){
      FileOutputStream out   =   null;
      try{
       File file            =    FileManager.getInstallationPropertiesFile();
       out                   =   new FileOutputStream(file);
       properties.store(out, "---No Comment---");
      }
      catch (Exception e){e.printStackTrace();}
      finally{
      }

   }
    
    public boolean makeStartStopFileExcecutable(){
         String srcStartStop    =   getInstallationStartStopFileAbsolutePath();
         File f                 =   new File(srcStartStop);

         String [] command      =   new String []{"chmod", "+x",f.getAbsolutePath()};
         String out             =   RunCommand.execute(command);

         return true;
    }
    public void    registerInstallation(){
        System.out.println("Register Installation");
        StringBuilder sb                    =   new StringBuilder();
        String SEPARATOR                    =   "_^_";
        String EMAIL                        =   "None";

        if (this.isEmailSubscribe()){
             if ( email== null ||  email.length() < 3){
                 EMAIL  = "None";
             }
             else{
                 EMAIL  = email;
            }
        }
        
        sb.append( EMAIL  );
        sb.append( SEPARATOR );
        sb.append( serverHostName );
        sb.append( SEPARATOR );
        sb.append( serverIP );
        sb.append( SEPARATOR );
        sb.append( getServerSoftware());
        sb.append( SEPARATOR );
        sb.append( BayesServerApp.VESRION);


        InstalationRegisterWorker register  =  new InstalationRegisterWorker();
        int aport                           =  URLManager.MASTER_SERVER_CGI_PORT;
        register.serverUrl                  =  URLManager.getMasterServerURL(aport);
        System.out.println(register.serverUrl );
        register.scriptPath                 =  URLManager.REGISTER_CGI_SCRIPT;
        register.request                    =  sb.toString();
        System.out.println("################");
        register.execute();

    }

   private  void  setAllDirWritable(boolean writable){
        File bin        =   FileManager.getBinDir();
        File sbin       =   FileManager.getSBinDir();
        File lib        =   FileManager.getLibDir();
        File system     =   FileManager.getSystemDir();
        File cgibin     =   FileManager.getCgiBinDir();
        File bayes      =   FileManager.getBayesDir();

        File [] dirs   =   new File[0];

        if (writable == true){ 
                dirs    =  new File [] {bin, sbin, lib,  cgibin, bayes, system,};
        }
        else{   
                dirs    =  new File [] {bin, sbin, lib, bayes, cgibin};
        }


        for (File dir : dirs) {setWritebale(dir, writable);}


   }
   public static void    setWritebale(File file, boolean writable){
        if (file.exists() == false){return;}
        String []command  =  null;

        if (file.isDirectory() == true){
            if (writable){
                command   = new String []{"chmod", "-R", "+w", file.getPath()};
            }
            else {
                command   = new String []{"chmod", "-R", "-w", file.getPath()};
            }
             RunCommand.execute( command);
         }
        else if(file.isFile() == true){
            if (writable){
                command   = new String []{"chmod", "u+w", file.getPath()};
            }
            else {
                command   = new String []{"chmod", "u-w", file.getPath()};
            }

            RunCommand.execute( command);
        }




    }

  
   public static boolean isFileExist (String str){
       boolean out                  =   false;
       try{
           if (str == null || str.isEmpty()){
            out                      =   false;
            }
            else{
                 File file              =    new File (str);
                 out                    =    file.exists();
                 
           }
        }
       catch (Exception ex){ex.printStackTrace();}
       finally{
           return out;
       }
   }
   public static boolean isDirExist (String str){
       boolean out                  =   false;
       try{
           if (str == null || str.isEmpty()){
            out                      =   false;
            }
            else{
                 File file              =    new File (str);
                 out                    =    file.exists()&&file.isDirectory();
                 
           }
        }
       catch (Exception ex){ex.printStackTrace();}
       finally{
           return out;
       }
   }
   
   
 
   
    public String    getInstallationCompeteMessage(){
      if (this.skipApacheSetup){return "";}
      if (this.isUbuntu()){return apache2.getSudoScriptToCompleteInstallation(virginConfFile);}
       StringBuilder sb         =   new StringBuilder();
       String dstStartStop      =   getDestinationStartStopFileAbsolutePath();
       String dstConfFile       =   getDestinationConfFileAbsolutePath();
       String srcStartStop      =   getInstallationStartStopFileAbsolutePath();
       String srcConfFile       =   getInstallationConfFileAbsolutePath();
       String docRoot           =   getDocumentRootAbsolutePath();

       File   docRootFile       =   new File( docRoot);
       File   linkFile          =   new File( docRoot,FileManager.BAYES);
       File   startStopFile     =   new File(  dstStartStop);
       File   confFile          =   new File(   dstConfFile);


       boolean docRootExist    =   docRootFile.exists();
       boolean linkFleExist    =   linkFile.exists();
       boolean startStopExist  =   startStopFile.exists();
       boolean confFileExist   =   confFile.exists();

      if(startStopExist && confFileExist){
         sb.append(dstStartStop + " stop");
         sb.append("\n");
      }


       sb.append("unalias cp" );
       sb.append("\n");
       sb.append("cp "+ srcConfFile + " "+dstConfFile );
       sb.append("\n");
       sb.append("cp "+ srcStartStop + " "+dstStartStop );
       sb.append("\n");
       
       if (docRootExist ==  false){
           sb.append("mkdir -p "+docRoot + "\n");
       }
        sb.append("cd "+ docRootFile.getPath() );
        sb.append("\n");

        sb.append("rm -f "+ FileManager.BAYES);
        sb.append("\n");


        String dir          =   FileManager.getBayesDir().getAbsolutePath();
       // sb.append("ln -s ~bayes/"+ FileManager.BAYES);
        sb.append("ln -s "+ dir);
        sb.append("\n");
       

    

       sb.append(dstStartStop + " start");
       sb.append("\n");


        return sb.toString();

   }
    public String    getFinaleStageHeader(){
       StringBuilder sb         =   new StringBuilder();
       sb.append("<html>");
        sb.append("CONGRATULATIONS - BAYESIAN SOFWARE WAS SUCCESSFULLY CONFIGURED!");
        sb.append("<br> <br>");
        sb.append("IF YOU ARE");
        sb.append("<FONT COLOR=RED> UPDATING </FONT>");
        sb.append("THE BAYESIAN SOFTWARE, THE UPDATE IS COMPLETED AND NO MORE ACTION IS  REQUIRED" );
        sb.append("<br> <br>");
        
     
        
        sb.append("IF YOU ARE");
        sb.append("<FONT COLOR=RED> INSTALLING </FONT>");
        sb.append("THE BAYESIAN SOFTWARE FOR THE FIRST TIME, YOU HAVE TO FINISH INSTALLATION BY CONFIGURING APACHE SERVER." );
        sb.append("<br>");
        sb.append("EXCUTE FOLLOWING SET OF COMMANDS AS ROOT" );
        sb.append("<br>");
        sb.append("<FONT COLOR=BLUE>(No instuctions are present, if you opted to skip apache configuration)</FONT>" );
        
       
        sb.append("</html>");

        return sb.toString();

   }
    
    
    
   public static void   getSystemProperties(){
        Properties p = System.getProperties ();
        java.util.Enumeration en = p.propertyNames();

        for (; en .hasMoreElements(); ) {


            String propName = (String)en.nextElement();
            String propValue = (String)p.getProperty(propName);
            System.out.println(propName + " =  "+ propValue);
        }
    }
   public static String getUserName(){
        return System.getProperty(USER_NAME).toString();
    }
  

   public void          recordSystemVariables(){
        userHome                =  FileManager.USER_HOME_DIR;
        installDir              =  userHome;
       
        osname                  =   System.getProperty(OS_NAME);

        /*
         * I need this in oder to set User Shell
         */
        try{
            unixenv                 =   RunCommand.execute(new String []{"env"});
        }
        catch(Exception e){
            e.printStackTrace();
            unixenv                 = "";
        }
        setShellValue();


        osarch                  =   RunCommand.execute("arch");
        if (osarch == null || osarch.length() < 1){
             osarch                =   RunCommand.execute(new String [] {"uname", "-m"});
        }


        if (osname.contains("Sun") || osname.contains("sun")){

            this.setPlatform(PLATFORM.SUN);
        
        }
        else if(osname.contains("linux") ||  osname.contains("Linux")){
            this.setPlatform(PLATFORM.LINUX);
        }
        System.out.println("OS name "+ osname);
         
        
        // is 64 bit
        boolean is64 = is64Bit();
        setOS64Bit(is64);
        System.out.println("OS is 64 bit = "+ is64);
        
        String osreleaseHOLD                        =   osrelease;
        switch (getPlatform()){
            case SUN :
                         osreleaseHOLD              =   findReleaseSun();
                         break;
            case LINUX :
                         osreleaseHOLD              =   findReleaseLinux();
                         break;
         }
         osrelease                                  =   osreleaseHOLD;
        
    
       setOSValid(getPlatform().isValid(osrelease));
       setOSArchValid(isValidHardware(osarch));

       String startDir      =   getPlatform().getCompilerDirGuess();
       setFortanDir(startDir);
       setcDir(startDir);

      

        String agroup       =   RunCommand.execute("groups");
        groupChoices        =   agroup.split("\\s+?");
        group               =   agroup;

        String id           =   RunCommand.execute("id");
        String parsedGroup  =   parseGroupName (id);
        if (parsedGroup != null){
           group            =   parsedGroup;
        }
        else {
           group            =   groupChoices[0].trim();
        }

   }
   public void          recordApacheVariables(){
       
        String docRutParentDirHOLD                  =   documentRootParenDir;
        String pidFileDirHOLD                       =   pidFileDir;
        String httpdCommandPathHOLD                 =   getHttpdCommandPath();
        String logDirHOLD                           =   this.getLogsDir();
        String serverootHOLD                        =   this.getServeroot();
        String osreleaseHOLD                        =   osrelease;
        switch (getPlatform()){

            case SUN :
                         docRutParentDirHOLD        =   Constants.DOCUMENTROOT_SUN4;
                         pidFileDirHOLD             =   Constants.PIDFILE_DIR_SUN4 ;
                         httpdCommandPathHOLD       =   Constants.HTTPD_EXECUATBLE_SUN4;
                         logDirHOLD                 =   Constants.LOGS_DIR_SUN4;
                         serverootHOLD              =   Constants.SERVERROOT_SUN4;
                         break;
           
            case LINUX :
                         docRutParentDirHOLD        =   Constants.DOCUMENTROOT_LINUXPC;   
                         pidFileDirHOLD             =   Constants.PIDFILE_DIR_LINUX ;
                         httpdCommandPathHOLD       =   Constants.HTTPD_EXECUATBLE_LINUXPC;
                         logDirHOLD                 =   Constants.LOGS_DIR_LINUX;
                         serverootHOLD              =   Constants.SERVERROOT_LINUXPC;
                         break;
        }
         osrelease                                  =   osreleaseHOLD;
         
        // check and assign  documentRootParenDir
        if (isDirExist(docRutParentDirHOLD)){
                 documentRootParenDir                        =   docRutParentDirHOLD;
        }
        
        // check and assign  pidFileDir 
        if (isDirExist(pidFileDirHOLD)){
                pidFileDir                                  =   pidFileDirHOLD;
        }
        
        // check and assign  httpdCommandPath 
        System.out.println("setting httpdCommandPath to "+httpdCommandPathHOLD);
        if (isFileExist(httpdCommandPathHOLD)){
                setHttpdCommandPath(httpdCommandPathHOLD);
        }
       
       // check and assign  logDir
        if (isDirExist(logDirHOLD)){
                setLogsDir(logDirHOLD );
        }
        
        // check and assign  serveroot
        if (isDirExist(serverootHOLD ) ){
               setServeroot(serverootHOLD );
        }
        
       
       // find virgin start/stop and config files
       setVirginConfFile(getPlatform().findVirginConfFile());
       setVirginStartStopFile(getPlatform().findVirginStartStopFile());


       setDestinationConfileDir     ( getPlatform().getDestinationDirForConfFile());
       setDestinationStartStopDir   ( getPlatform().getDestinationDirForStartStopFile());

   }
   public void          recordNetworkVariables (){
        try {
           
            
           List<InetAddress> inetAdresses =    getAllLocalIPs();
            System.out.println("Found "+inetAdresses.size() + " valid interfaces.");
            for (InetAddress inetAddress : inetAdresses) {
                System.out.println(inetAddress.getCanonicalHostName()+ 
                        "  "+inetAddress.getHostAddress());
            }
            /*
            InetAddress InetAddress        = InetAddress.getLocalHost();
            serverHostName            = InetAddress.getHostName() ;
            System.out.println( "Canonical name "+ InetAddress.getCanonicalHostName());
            setServerIP(InetAddress.getHostAddress());*/
            InetAddress InetAddress         =   inetAdresses.get(0);
            serverHostName                  =   InetAddress.getHostName();
            serverIP                        =   InetAddress.getHostAddress();
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(InstallerGui.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   public static List<InetAddress> getAllLocalIPs()
    {
        LinkedList<InetAddress> listAdr = new LinkedList<InetAddress>();
        try
        {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            if (nifs == null) return listAdr;
 
            while (nifs.hasMoreElements())
            {
                NetworkInterface nif = nifs.nextElement();
                // We ignore subinterfaces - as not yet needed.
 
                Enumeration<InetAddress> adrs = nif.getInetAddresses();
                while (adrs.hasMoreElements())
                {
                    InetAddress adr = adrs.nextElement();
                    if (adr != null && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress()))
                    {
                        listAdr.add(adr);
                    }
                }
            }
            return listAdr;
        }
        catch (SocketException ex)
        {
            System.out.println("No IP address available");
            ex.printStackTrace();
            return listAdr;
        }
    }

   public void          recordCompilerVariables(){
      File file         =   FileManager.getFortranSetupFile();
      recordCompilerVariables(file);
   }
   public void          recordCompilerVariables(File file){
    resetCompilers();
    if (file.exists() == false){return;}
    String source          =    WriteSetupFortran.SOURCE;
    String echo            =    WriteSetupFortran.ECHO;
    String path            =    WriteSetupFortran.PATH;
    String setenv          =    WriteSetupFortran.SETENV;


      String content    =   IO.readFileToString(file);
      if(content == null){ return;}

      Scanner scanner       =   new Scanner(content);
      String fpath          =   null;
      String cpath          =   null;
      String fcomp          =   null;
      String ccomp          =   null;
      String fdir           =   null;
      String cdir           =   null;

     

     String message                         =   String.format("Error, while parsing FostranSetup\n" +
                                               "%s file.\n" +
                                               "All compilers are set to %s",file.getPath(), NOCOMPILER);


      try{
        // to spit line based on white spacew or ':' introduce regex
        String regex                          =   "[\\s+, :]";
        while( scanner.hasNextLine()){
            String line                       = scanner.nextLine();
            System.out.println("Parsing line: "+ line);
            String [] tokens                  =  line.split( regex);
          if (tokens.length < 2){continue;}

          String  command                   =   tokens [0];
          String  arg1                      =   tokens [1];


          if        (command.equalsIgnoreCase(source)){
              if         (arg1.endsWith(IntelCompiler.FORTAN_SETUP_LINUX32)){
                fpath   =   arg1;
              }
              else if   (arg1.endsWith(IntelCompiler.FORTAN_SETUP_LINUX64))
              {
                fpath   =   arg1;
              }
              else if   (arg1.endsWith(IntelCompiler.C_SETUP_LINUX32))
              {
                cpath   =   arg1;
              }
               else if   (arg1.endsWith(IntelCompiler.C_SETUP_LINUX64))
              {
                cpath   =   arg1;
              }
              else if(fpath == null) {    fpath   =   arg1;       ;}
              else                   {    cpath   =   arg1;     ;}
          }
          else if   (command.equalsIgnoreCase(setenv) && arg1.equalsIgnoreCase(path)){
             String  arg2                   =   tokens [2];
             if (fdir == null) {    fdir    =   arg2; }
             else              {    cdir    =   arg2; }
          }
          else if   (command.equalsIgnoreCase(echo) ){
                                    fcomp   = tokens [1]; 
                                    ccomp   = tokens [2]; 
          }
        }

      }
      catch(Exception exp){
          exp.printStackTrace();
          DisplayText.popupErrorMessage(message);
          return;
      }
      finally{
         scanner.close();
      }


     // for fortan
     if (fpath != null && fcomp != null){
        File scriptFile         =   new File (fpath);
        if ( scriptFile.exists() ){
            File dir            =   scriptFile.getParentFile();
            File cmp            =   new File (dir, fcomp);
            if (cmp.exists()){
                 this.setFortanDir(dir.getAbsolutePath());
                 this.setFortanConfigSrcipt( scriptFile.getName());
                 this.setFortanCompilerName(fcomp);
            }
       }

     }else if (fdir != null && fcomp != null){
           File cmp       =   new File (fdir, fcomp);
           if  (cmp .exists() ){
                this.setFortanDir(fdir);
                this.setFortanCompilerName(fcomp);
            }
     }


      // for C
     if (cpath != null && ccomp != null){
        File scriptFile        =   new File (cpath);
        if (scriptFile.exists() ){
            File dir            =   scriptFile.getParentFile();
            File cmp            =   new File (dir, ccomp);
            if (cmp.exists()){
                 this.setcDir(dir.getAbsolutePath());
                 this.setcConfigScript(scriptFile.getName());
                 this.setcCompilerName(ccomp);

            }
       }

     }
     else if (cdir != null  && ccomp != null){
           File cmp      =   new File (cdir, ccomp);
           if  (cmp.exists() ){
                this.setcDir(cdir);
                this.setcCompilerName(ccomp);

            }
     }





   }


   public void          loadInstallationInfo (){
       File instalinfo                = FileManager.getInstallationInfoFile();
       System.out.println("Installation info File "+ instalinfo .getPath());
       System.out.println("Does installation Info File exist =  "+instalinfo.exists());
       if (instalinfo.exists() == false){return;}

       InstallationInfo info          = InstallationInfo.loadFromFile(instalinfo);
       if (info.isLoaded() == false){return;}

       this.email                   =  info.getEmail();
       this.setPasswordProtected(info.isPassword());
       this.setEmailSubscribe(info.isSubscribeForUpdates());
       this.setServerQueue(info.getQueue());
       this.port                    =  info.getPort();

       this.setInstallInfo(info);
      
   }

   public static String     findReleaseSun(){
       String out           =    null;
       File file            =   new File("/etc/release");
       Scanner scanner      =   null;

       try {
              scanner       =   new Scanner(file);
            if (scanner.hasNextLine() == true){
                out         =    scanner.nextLine().trim();
            }
       }
       catch (FileNotFoundException ex) {
             ex.printStackTrace();
       }
       finally{
        if(scanner!= null) {scanner.close();}
        return out;
       }
   }
   public static String     findReleaseLinux(){
       String out           =   null;
       File dir             =   new File ("/etc");
       if (dir.exists() == false || dir.isDirectory() == false){
                return      out;
       }
       File [] files         =   dir.listFiles( new  ReleaseFileFilter());


       Scanner scanner      =   null;
       File releaseFile     =   files[0];
       try {
              scanner       =   new Scanner(releaseFile );
            if (scanner.hasNextLine() == true){
                out         =    scanner.nextLine().trim();
            }
            
            System.out.println("Release File = "+releaseFile.getPath());
            System.out.println("Release parsed from release file  \""+ out+"\"");
       }
       catch (FileNotFoundException ex) {
             ex.printStackTrace();
       }
       finally{
        if(scanner!= null) {scanner.close();}
        return out;
       }
   }
   public void              setShellValue(){
        int ind = unixenv.indexOf("SHELL");
        if(ind < 0){return;}

        ind     =   unixenv.indexOf("=", ind);
        if(ind < 0){return;}

        int indEnd = unixenv.indexOf("\n", ind);
        if(indEnd < 0){return;}

        shell = unixenv.substring(ind +1, indEnd).trim();

   }
  
    public static boolean isEmailValid(String email){
            boolean isValid = false;

        /*
        Email format: A valid email address will have following format:
            [\\w\\.-]+: Begins with word characters, (may include periods and hypens).
            @: It must have a '@' symbol after initial characters.
            ([\\w\\-]+\\.)+: '@' must follow by more alphanumeric characters (may include hypens.).
            This part must also have a "." to separate domain and subdomain names.
            [A-Z]{2,4}$ : Must end with two to four alaphabets.
        (This will allow domain names with 2, 3 and 4 characters e.g pa, com, net, wxyz)

        Examples: Following email addresses will pass validation
        abc@xyz.net; ab.c@tx.gov
        */

        //Initialize reg ex for email.
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        //Make the comparison case-insensitive.
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches()){
            isValid = true;
        }
        return isValid;
}
    public static boolean isQueueValid(String queue){
          if(queue == null || queue.isEmpty()){
            return false;
          }


        String expression = "\\S+";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(queue);
        if(matcher.matches()){
           return true;
        }
        else{
             return false;
        }

}
    public boolean isValidHardware(String hdwarename){
        String [] validHardware  =  PLATFORM.HARDWARE;
        hdwarename               =  hdwarename.trim();
        for (String curHdware : validHardware) {
            if (curHdware.equalsIgnoreCase(hdwarename)){ return true;}
            if (curHdware.startsWith(hdwarename)){ return true;}
        }

        return false;

    }
    public boolean isCshell(){
         if (shell.endsWith("/csh" )){return true;}
         if (shell.endsWith("/tcsh" )){return true;}
         return false;
    }
    public boolean is64Bit() {
        boolean out             =   false;
        try{
            if (osarch  == null){
                 osarch                =   RunCommand.execute(new String [] {"getconf", "LONG_BIT"});
            }

            out                 =   (osarch!=null) && (osarch .contains("64"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            return out;
        }
    }
    public boolean is64BitOld() {
        boolean out             =   false;
        try{
            if (osarch  == null){
                 osarch                =   RunCommand.execute(new String [] {"uname", "-m"});
            }

            out                 =   (osarch!=null) && (osarch .indexOf("64")!=-1);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            return out;
        }
    }
    public boolean isValidUser() {
       String user     = getUserName();
       if (user.equals("root")){return false;}
       else{return true;}
    }
    public boolean isUbuntu (){
        boolean out     =    false;
        if (osrelease != null){
           out =  osrelease.contains(PLATFORM.UBUNTU);
        }
        return out;
    }
    
    private void setupApache2(){
        apache2.setPort(port);
        apache2.setGroup(group);
        apache2.setEmail(email);
        apache2.setScriptAlias(FileManager. getCgiBinDir().getAbsolutePath());
        apache2.setConfigFileStorageDir(FileManager.getSystemDir());
    }
   
    private String parseGroupName (String resultOfIdCommand){
        Pattern p   =   Pattern.compile("gid=\\d+?\\(");
        Matcher m   =   p.matcher( resultOfIdCommand);
        String out  =   null;
        boolean b   =   m.find();
        if (b == true){
            String  temp = resultOfIdCommand.substring(m.start());
            int i       = temp.indexOf("(");
            int j       = temp.indexOf(")");
            out = temp.substring(i+1,j);
        }
        return out;
   }
   
  
    public File getLocalInstallationGZipFile(){
         File file =  new File(installDir, installationKitName);
         return file;
    }
    public File getLocalInstallationTarFile(){
        int ind             =  installationKitName.lastIndexOf(".");
        String tarFileName  =   installationKitName.substring(0, ind );
        File file =  new File(installDir, tarFileName);
        return file;
    }
    public String getLocalBayesServerURL(){
        String url      = "http://"+ this.getServerIP() +":" + this.getPort()+"/";
        return url;
    }
  
   

    public static void main(String [] args){
    Installer i = new Installer();
    //i.email  = "inside bmrl network";
      // i.registerInstallation();
        System.out.println(i.getFinaleStageHeader());
     // File d = new File ("/Users/apple/Desktop/FortranSetup");
      try{
          Thread.sleep(5000l);
      }
      catch(Exception e){e.printStackTrace();}
        //System.out.println("Dones");
      //new Installer("a").recordCompilerVariables(d);
   
    }


     public void     setNoFortanCompiler(){
            setFortanDir(Installer.NOCOMPILER);
            setFortanCompilerName( Installer.NOCOMPILER);
            setFortanConfigSrcipt( Installer.NOCOMPILER);
     };
     public void     setNoCCompiler(){
            setcDir(Installer.NOCOMPILER);
            setcCompilerName( Installer.NOCOMPILER);
            setcConfigScript( Installer.NOCOMPILER);
     };
     
    public void setPortAndUpdateDependencies(int port) {
        // update port
        this.port               =   port;
        
        // updates names that are dependent on porrt

        syncPidFileNameWithPort();
        syncDocumentRootNameWithPort();
        syncDestinationConfFileNameWithPort();
        syncDestinationStartStopFilenameWithPort();
        syncErrorLogFilenameWithPort();
        syncCustomLogFilenameWithPort();
         
    }
    public int getPort() {
        return port;
    }
    public String getPidFileNameForPort(int port){
        String name ="httpd"    +   port + ".pid";
        return name;
    }
    public void syncPidFileNameWithPort(){
        int aport               =   this.getPort();
        String name             =   getPidFileNameForPort (aport);
        setPidFileName (name);
    }
    public String getDocumentRootNameWithPort(int port){
     String name ="htdocs"   +   port;
     return name;
    }
    public void syncDocumentRootNameWithPort(){
        int aport               =   this.getPort();
        String name             =   getDocumentRootNameWithPort (aport);
        setDocumentRoot  (name);
    }
    public String getDestinationConfFileNameWithPort(int port){
     String name ="httpd"    +   port + ".conf";
     return name;
    }
    public void syncDestinationConfFileNameWithPort(){
        int aport               =   this.getPort();
        String name             =    getDestinationConfFileNameWithPort (aport);
        setDestinationConfFileName  (name);
    }
    public String getDestinationStartStopFilenameWithPort(int port){
     String name ="S85httpd" +   port;
     return name;
    }
    public void syncDestinationStartStopFilenameWithPort(){
        int aport               =   this.getPort();
        String name             =  getDestinationStartStopFilenameWithPort (aport);
        setDestinationStartStopFilename  (name);
    }
    public String getErrorLogFilenameWithPort(int port){
     String name ="error_log" +   port;
     return name;
    }
    public void syncErrorLogFilenameWithPort(){
        int aport               =   this.getPort();
        String name             =   getErrorLogFilenameWithPort (aport);
        setErrorLog (name);
    } 
    public String getCustomLogFilenameWithPort(int port){
     String name ="access_log"  +   port;
     return name;
    }
    public void syncCustomLogFilenameWithPort(){
        int aport               =   this.getPort();
        String name             =   getCustomLogFilenameWithPort (aport);
        this.setCustomLog(name);
    } 
    
    
    public String   getPidFileAbsolutePath(){
        if (getPidFileDirPath() == null || getPidFileDirPath().length() ==0){
            return "";
        }

        File f = new File ( getPidFileDirPath(), getPidFileName());
        return f.getAbsolutePath();
    }
    public String   getDocumentRootAbsolutePath(){
        if (  getDocumentRootParentDir() == null ||  getDocumentRootParentDir().length() ==0){
            return "";
        }

        File f = new File (  getDocumentRootParentDir(), getDocumentRoot());
        return f.getAbsolutePath();
    }
    public String   getDestinationConfFileAbsolutePath(){
        if ( getDestinationConfileDir()  == null ||  getDestinationConfileDir() .length() ==0){
            return "";
        }

        File f = new File (   getDestinationConfileDir(), getDestinationConfFileName());
        return f.getAbsolutePath();
    }
    public String   getDestinationStartStopFileAbsolutePath(){
        if (  getDestinationStartStopDir() == null ||  getDestinationStartStopDir().length() ==0){
            return "";
        }

        File f = new File (   getDestinationStartStopDir(), getDestinationStartStopFilename());
        return f.getAbsolutePath();
    }
    public String   getInstallationConfFileAbsolutePath(){
        File sysDir          =    FileManager.getSystemDir();
        if ( getDestinationConfileDir() .length() ==0){
            return "";
        }

        File f = new File (  sysDir, getDestinationConfFileName());
        return f.getAbsolutePath();
    }
    public String   getInstallationStartStopFileAbsolutePath(){
        File sysDir          =    FileManager.getSystemDir();
        if (  getDestinationStartStopDir().length() ==0){
            return "";
        }

        File f = new File ( sysDir, getDestinationStartStopFilename());
        return f.getAbsolutePath();
    }
    public String   getFortanCompilerAbsolutePath(){
        if (  getFortanDir().equals(NOCOMPILER) ||  getFortanCompilerName().equals(NOCOMPILER)){
            return NOCOMPILER;
        }

        File f = new File (  getFortanDir(), getFortanCompilerName());
        return f.getAbsolutePath();
    }
    public String   getFortanConfigAbsolutePath(){
        if (  getFortanDir().equals(NOCOMPILER) || getFortanConfigSrcipt().equals(NOCOMPILER)){
            return NOCOMPILER;
        }

        File f = new File (  getFortanDir(), getFortanConfigSrcipt());
        return f.getAbsolutePath();
    }
    public String   getCCompilerAbsolutePath(){
        if (  getcDir().equals(NOCOMPILER) ||  getcCompilerName().equals(NOCOMPILER)){
            return NOCOMPILER;
        }

        File f = new File (  getcDir(), getcCompilerName());
        return f.getAbsolutePath();
    }
    public String   getCConfigAbsolutePath(){
        if (  getcDir().equals(NOCOMPILER) ||   getcConfigSrcipt().equals(NOCOMPILER)){
            return NOCOMPILER;
        }

        File f = new File (  getcDir(),  getcConfigSrcipt());
        return f.getAbsolutePath();
    }
    public String   getCustomLogAbsolutePath(){
        String logdir           =   getLogsDir();
        if (   logdir == null || logdir.length() ==0){
            return "";
        }

        File f = new File ( logdir, getCustomLog());
        return f.getAbsolutePath();
    }
    public String   getErrorLogAbsolutePath(){
        String logdir           =   getLogsDir();
        if (   logdir  == null ||   logdir .length() ==0){
            return "";
        }

        File f = new File (  logdir , getErrorLog());
        return f.getAbsolutePath();
    }



    public String   getPidFileName() {
        return pidFileName;
    }
    public void     setPidFileName(String pidFileName) {
        this.pidFileName = pidFileName;
    }
    public String   getPidFileDirPath() {
        return pidFileDir;
    }
    public void     setPidFileDirPath(String pidFileDirPath) {
        this.pidFileDir = pidFileDirPath;
    }

    public String   getDocumentRoot() {
        return documentRoot;
    }
    public void     setDocumentRoot(String documentRootFile) {
        this.documentRoot = documentRootFile;
    }
    public String   getDocumentRootParentDir() {
        return documentRootParenDir;
    }
    public void     setDocumentRootParentDir(String documentRootParenDir) {
        this.documentRootParenDir = documentRootParenDir;
    }

    public String   getDestinationConfFileName() {
        return destimationConfFileName;
    }
    public void     setDestinationConfFileName(String confileName) {
        this.destimationConfFileName = confileName;
    }
    public String   getDestinationConfileDir() {
        return destinationDirForConfFile;
    }
    public void     setDestinationConfileDir(String confileDir) {
        this.destinationDirForConfFile = confileDir;
    }


    public String   getDestinationStartStopDir() {
        return destinationDirForStartStopFile;
    }
    public void     setDestinationStartStopDir(String startStopDir) {
        this.destinationDirForStartStopFile = startStopDir;
    }
    public String   getDestinationStartStopFilename() {
        return destinationStartStopFilename;
    }
    public void     setDestinationStartStopFilename(String startStopFilename) {
        this.destinationStartStopFilename = startStopFilename;
    }

    public String   getLogsDir() {
        return logsDir;
    }
    public void     setLogsDir(String logsDir) {
        this.logsDir = logsDir;
    }
    public boolean  isLogsDirValid() {
        if (logsDir == null || logsDir.isEmpty() ){return false;}
        else{
            return true;
        }
    }
   
    public String   getServeroot() {
        return serveroot;
    }
    public void     setServeroot(String serveroot) {
        this.serveroot = serveroot;
    }


    public boolean isCCompilerSet(){
        return !getCCompilerAbsolutePath().equals(NOCOMPILER);
    }
    public boolean isFortanCompilerSet(){
        return !getFortanCompilerAbsolutePath().equals(NOCOMPILER);
    }
    public boolean isBMRLMachine(){
        boolean isBmrl  =   false;
        String curHost  = getServerIP();
        String bmrlHost = Constants.BMRL_MASK;

        if (curHost != null){
               isBmrl = curHost.startsWith(bmrlHost);

        }

        return isBmrl;
    }


    public void    resetFortranCompilers(){
        this.setFortanCompilerName(Installer.NOCOMPILER);
        this.setFortanDir(Installer.NOCOMPILER);
        this.setFortanConfigSrcipt(NOCOMPILER);
    }
    public void    resetCCompilers(){
        this.setcCompilerName(Installer.NOCOMPILER);
        this.setcDir(Installer.NOCOMPILER);
        this.setcConfigScript(NOCOMPILER);
    }
    public void    resetCompilers(){
        resetFortranCompilers();
        resetCCompilers();
    }

    /********* VIRGIN FILES ******************/
    public File     getVirginStartStopFile() {
        return virginStartStopFile;
    }
    public String   getVirginStartStopFileName() {
           File httpdFile = getVirginStartStopFile();
           if (httpdFile ==  null){return "";}
           else {return httpdFile.getAbsolutePath();}
    }
    public void     setVirginStartStopFile(File virginHTTPDFIle) {
        this.virginStartStopFile = virginHTTPDFIle;
    }
   
    public File     getVirginConfFile() {
        return virginConfFile;
    }
    public String   getVirginConfFileName() {
           File confFile = getVirginConfFile();
           if (confFile ==  null){return "";}
           else {
               return confFile.getAbsolutePath();
           }
    }
    public void     setVirginConfFile(File virginConfFIle) {
        this.virginConfFile = virginConfFIle;
    }



    public String   getFortanCompilerName() {
        return fortanCompilerName;
    }
    public void     setFortanCompilerName(String fortanCompilerName) {
        this.fortanCompilerName = fortanCompilerName;
    }


    public String   getcCompilerName() {
        return cCompilerName;
    }
    public void     setcCompilerName(String cCompilerName) {
        this.cCompilerName = cCompilerName;
    }

    public String   getFortanConfigSrcipt() {
        return fortanConfigSrcipt;
    }
    public void     setFortanConfigSrcipt(String fortanConfigSrcipt) {
        this.fortanConfigSrcipt = fortanConfigSrcipt;
    }

    public String   getcConfigSrcipt() {
        return cConfigSrcipt;
    }
    public void     setcConfigScript(String cConfigSrcipt) {
        this.cConfigSrcipt = cConfigSrcipt;
    }

    public String   getFortanDir() {
        return fortanDir;
    }
    public void     setFortanDir(String fortanDir) {
        this.fortanDir = fortanDir;
    }

    public String   getcDir() {
        return cDir;
    }
    public void     setcDir(String cDir) {
        this.cDir = cDir;
    }

    public PLATFORM getPlatform() {
        return platform;
    }
    public void     setPlatform(PLATFORM platform) {
        this.platform = platform;
    }

    public String   getErrorLog() {
        return errorLog;
    }
    public void     setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    public String   getCustomLog() {
        return customLog;
    }
    public void     setCustomLog(String customLog) {
        this.customLog = customLog;
    }

    public boolean  isOSValid() {
        return OSValid;
    }
    public void     setOSValid(boolean OSValid) {
        this.OSValid = OSValid;
    }

    public boolean  isOSArchValid() {
        return OSArchValid;
    }
    public void     setOSArchValid(boolean OSArch) {
        this.OSArchValid = OSArch;
    }

    public InstallationInfo getInstallInfo() {
        return installInfo;
    }
    public void             setInstallInfo(InstallationInfo installInfo) {
        this.installInfo = installInfo;
    }

    public String           getInstallMessage() {
        return installMessage;
    }
    public void             setInstallMessage(String installMessage) {
        this.installMessage = installMessage;
    }

    public String           getServerSoftware() {
        return serverSoftware;
    }
    public void             setServerSoftware(String serverSoftware) {
        this.serverSoftware = serverSoftware;
    }

    public String           getServerIP() {
        return serverIP;
    }
    public void             setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void             setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    public boolean          isPasswordProtected() {
        return passwordProtected;
    }
    public void             setPasswordProtected(boolean passwordProtected) {
        this.passwordProtected = passwordProtected;
    }

    public boolean          isEmailSubscribe() {
        return emailSubscribe;
    }
    public void             setEmailSubscribe(boolean emailSubscribe) {
        this.emailSubscribe = emailSubscribe;
    }

    public String           getServerQueue() {
        return serverQueue;
    }
    public void             setServerQueue(String serverQueue) {
        this.serverQueue = serverQueue;
    }

    public String           getShell() {
        return shell;
    }
    public void             setShell(String shell) {
        this.shell = shell;
    }

   
    public boolean isOS64Bit() {
        return OS64Bit;
    }
    void setOS64Bit(boolean OS64Bit) {
        this.OS64Bit = OS64Bit;
    }

    public String getHttpdCommandPath() {
        return httpdCommandPath;
    }

    public void setHttpdCommandPath(String httpdCommandPath) {
        this.httpdCommandPath = httpdCommandPath;
    }

    public boolean isSkipApacheSetup() {
        return skipApacheSetup;
    }

    public void setSkipApacheSetup(boolean skipApacheSetup) {
        this.skipApacheSetup = skipApacheSetup;
    }

  
    public Apache2 getApache2() {
        return apache2;
    }

    public void setApache2(Apache2 apache2) {
        this.apache2 = apache2;
    }

    
    
    

     class ConfigFileFilter implements java.io.FilenameFilter{
        public boolean accept(File dir, String name)    {
            boolean accept = name.endsWith(".conf");
            return accept;


         }
     }
     static class ReleaseFileFilter implements java.io.FilenameFilter{
        public boolean accept(File dir, String name)    {
            boolean accept  = false;
            if (name.endsWith("release")){
                accept = true;
            }
            else if (name.endsWith("issue")){
                accept = true;
            }
            else {}
            return accept;


         }
     }
     static class LocalScriptFilter implements java.io.FilenameFilter{
        public boolean accept(File dir, String name)    {
            for (String script : LOCAL_SCRIPTS) {
                if (name.equals(script)){return true;}
            }
            return false;


         }
     }
    
}
