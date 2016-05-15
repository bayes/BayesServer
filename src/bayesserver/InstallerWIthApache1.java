/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;

import java.io.File;
import java.util.Properties;

public class InstallerWIthApache1 extends BaseInstaller{
    
@Override  
public boolean isReadyToInstallApache(){
        String message;
    
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
        
       return true;
   }


@Override
public String    getInstallationCompeteMessage(){
      if (this.skipApacheSetup){return "";}
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
 @Override
 public void updateAndStoreInstallationProperties(){
    Properties p      =   this.properties;
    writeProperties();
}
 
@Override
public void readFromInstallationProperties(){
      readProperties();
}

@Override
public void apacheSetup(String errormessage) throws Exception{
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

    
    public static void main(String [] args){
    InstallerWIthApache1 i = new InstallerWIthApache1();
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
}
