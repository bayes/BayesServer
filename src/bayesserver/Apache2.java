/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesserver;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utilities.IO;

/**
 *
 * @author apple
 */
public class Apache2 {
   private  File apache2Dir                 =   null;
   private final String EOL                =   System.getProperty("line.separator");
   private final String APACHE2_CONF        =   "apache2.conf";
   private final String ENVVARS             =   "envvars";
   private final String MAGIC               =   "magic";
   private final String PORT_CONF           =   "ports.conf";
   private final String CONF_D_DIR          =   "conf.d";
   private final String DEFAULT_WEBSITE     =   "default";
   private final String MODS_AVAIALBLE_DIR  =   "mods-available";
   private final String MODS_CONFIGURE_DIR  =   "mods-enabled";
   private final String SITE_AVAILABLE_DIR  =   "sites-available";
   private final String SITE_ENABLED_DIR    =   "sites-enabled";
   private final String [] apache2Files     =   {
       APACHE2_CONF,ENVVARS,MAGIC, PORT_CONF 
   };
    private final String [] apache2dirs     =   {
      CONF_D_DIR, MODS_AVAIALBLE_DIR , MODS_CONFIGURE_DIR, SITE_AVAILABLE_DIR, SITE_ENABLED_DIR
   };
   private int port                                 =   8080;
   private String user                              =   System.getProperty("user.name").toString();
   private String group                             =   "";
  
   private String scriptAlias                       =   "";
   private String email                             =   "";
   private File   configFileStorageDir              =   null;
   private File  documentRootDir                    =   new File ("/var/www/");
   
   
   
   private String inegrityCheckError                =   "";
   private String configFileWriteError                    =   "";
   private String envvarsContent                    =   "";
   private String  portsContent                     =   "";
   private String siteContent                       =   "";
   private boolean valid                                    =   false;
   
    public Apache2(){
   }
   public Apache2(File file){
       this.apache2Dir = file;
   }
   
   public boolean checkApache2InstanceIntegrity(){
       setValid(false);
       setInegrityCheckError("");
       String msg               =   null;
       try{
           if (getApache2Dir() == null){
               throw new Exception("Apache2 directory is null.");
           }
           else if(getApache2Dir().exists() == false){
               msg  =   String.format("Apache instance directory %s is not a "
                       + "a valid directory",getApache2Dir().getPath() );
               throw new Exception(msg);
           
           }
           else if(getApache2Dir().isDirectory() == false){
               msg  =   String.format("%s is not a "
                       + "directory",getApache2Dir().getPath() );
               throw new Exception(msg);
           
           }
           
           // check for all directories
           for (String dirname : apache2dirs) {
               File dir       =    new File (getApache2Dir(),dirname );
               if (dir.exists() == false || dir.isDirectory() == false){
                msg  =   String.format("Instance of apache2 %s appears"
                       + "to be invalid. Directory %s doesn't exist or it is not a directory",
                       getApache2Dir().getPath(),
                       dir.getPath() );
               throw new Exception(msg);
               }
              
           }
           
           // check all files
           for (String filename : apache2Files) {
               File file       =    new File (getApache2Dir(),filename );
               checkFile(file);
           }
           
           // check default site file
           File defSite                 =    getDefaultSiteFile();
           checkFile(defSite);
           
           // check presence of start/stop 
           File startstop                 =   this.getApache2StartStopFile();
           if (startstop  .exists() == false ||startstop  .isFile() == false){
                 msg  =   String.format("Start/Stop file %s for apache2 instance %s is not found.",
                startstop.getPath(),  getApache2Dir().getPath());
       throw new Exception(msg);
       }
           checkFile(startstop );
           
            setValid(true);
      }
       catch (Exception e){
            setValid(false);
            setInegrityCheckError(e.getMessage());
           e.printStackTrace();
       }
       finally{
           return isValid() ;
       }
   }
   public File getDefaultSiteFile(){
          File sitesDir            =   new File (getApache2Dir(),SITE_AVAILABLE_DIR  );
          return getDefaultSiteFile(sitesDir );
    }
   public File getDefaultSiteFile(File dir){
       File defSite             =   null;
       try{
            defSite                 =   new File (dir ,DEFAULT_WEBSITE );
       }
       catch(Exception e){e.printStackTrace();}
       finally {return defSite;}
      
   }
   public File getEnvvarsFile(){
      return getEnvvarsFile(getApache2Dir());
      
   }
   public File getEnvvarsFile(File dir){
       File file            =   null;
       try{
            file         =   new File (dir,ENVVARS   );
       }
       catch(Exception e){e.printStackTrace();}
       finally {return file;}
      
   }
   public File getPortConfFile(){
       return getPortConfFile(getApache2Dir());
   }
   public File getPortConfFile(File dir){
       File file            =   null;
       try{
            file         =   new File (dir,PORT_CONF  );
       }
       catch(Exception e){e.printStackTrace();}
       finally {return file;}
      
   }
   public File getApache2StartStopFile (){
       File file            =   null;
       try{
           String dir       =   "/etc/init.d/"; 
           file         =   new File (dir,this.apache2Dir.getName()  );
       }
       catch(Exception e){e.printStackTrace();}
       finally {return file;}
      
   }
   
   public File generateDocumentRoot(){
       String out   =   documentRootDir.getAbsolutePath();
       out  = out + "/";
       out  = out+ "htdocs"+ port;
       return new File (out);
   
   }
   public void checkFile(File file)throws Exception{
       String msg           =   null;
       if (file.exists() == false || file.isFile() == false){
        msg  =   String.format("Instance of apache2 %s appears"
               + "to be invalid. File %s doesn't exist or it is not a file",
               getApache2Dir().getPath(),
               file.getPath() );
       throw new Exception(msg);
       }
       if (file.canRead() == false){
        msg  =   String.format("Error is encoutered. File %s does not have read permission.",
                file.getPath() );
       throw new Exception(msg);
       }
   }
   
   public boolean updateSiteContent (File file){
      boolean out               =   true;
      siteContent               =   "";
      StringBuilder sb          =   new StringBuilder();
      
      try{
          Scanner scanner   =   new Scanner(file);
          while (scanner.hasNextLine()){
              String line   =   scanner.nextLine();
              if (line.matches( "\\s*<VirtualHost.+>\\s*")){
                  line             = "<VirtualHost *:" + this.port + ">";
              }
               else  if (line.matches( "\\s*ServerAdmin.+")){
                  String regex     =  "ServerAdmin.+";
                  String replace   =   "ServerAdmin "+ this.email;
                  line             =  line.replaceAll(regex, replace);
              }
              else  if (line.matches( "\\s*DocumentRoot.+")){
                  String regex     =  "DocumentRoot.+";
                  String replace   =   "DocumentRoot " + this.generateDocumentRoot();
                  line             =  line.replaceAll(regex, replace);
              }
              else if (line.matches( "\\s*ScriptAlias.+")){
                  String regex     =   "ScriptAlias.+";
                  String replace   =   "ScriptAlias /cgi-bin/ "  + this.getScriptAlias()+ "/";
                  line             =  line.replaceAll(regex, replace);
              }
              sb.append(line);
              sb.append( EOL );
              
          }
          siteContent        =   sb.toString();
      }
      catch (Exception e){
          out                   =   false;
          siteContent        =   "";
          e.printStackTrace();
      }
      finally {
            return out;
      }
 
   }
   public boolean updateEnvvarsContent (File file){
      boolean out               =   true;
      envvarsContent            =   "";
      StringBuilder sb          =   new StringBuilder();
      String APACHE_RUN_USER    = "APACHE_RUN_USER=";
      String APACHE_RUN_GROUP   = "APACHE_RUN_GROUP=";
      
      try{
          Scanner scanner   =   new Scanner(file);
          while (scanner.hasNextLine()){
              String line   =   scanner.nextLine();
              if (line.contains(APACHE_RUN_USER)){
                  String regex      = APACHE_RUN_USER+"\\S+";
                  String replace    =  APACHE_RUN_USER+ this.user;
                  line             =  line.replaceAll(regex, replace);
              }
              else if (line.contains(APACHE_RUN_GROUP)){
                  String regex      = APACHE_RUN_GROUP+"\\S+";
                  String replace    =  APACHE_RUN_GROUP+ this.group;
                  line             =  line.replaceAll(regex, replace);
              }
              sb.append(line);
              sb.append( EOL );
              
          }
          envvarsContent        =   sb.toString();
      }
      catch (Exception e){
          out                   =   false;
          envvarsContent        =   "";
          e.printStackTrace();
      }
      finally {
            return out;
      }
 
   }
   public boolean updatePortsConfContent(File file){
      boolean out               =   true;
      portsContent              =   "";
      StringBuilder sb          =   new StringBuilder();
     
      try{
           sb.append("NameVirtualHost *:" + port);
           sb.append( EOL );
           sb.append( "Listen " +port);
           sb.append( EOL );
           portsContent       =   sb.toString();
      }
      catch (Exception e){
          out                   =   false;
          portsContent        =   "";
          e.printStackTrace();
      }
      finally {
            return out;
      }
 
   }
   public void    generateConfigurationFileContents(){
       updateEnvvarsContent(this.getEnvvarsFile() );
       updatePortsConfContent(this.getPortConfFile() );
       updateSiteContent(this.getDefaultSiteFile());
  }
   
   public boolean writeConfiguratioFiles (){
       boolean out                  =   true;
       configFileWriteError               =   "";
       try{
            // make sure we have file have modifoed configuration files
            generateConfigurationFileContents();
            
            File envvarsFile         =   this.getEnvvarsFile(configFileStorageDir); 
            File portConfigFile      =   this.getPortConfFile(configFileStorageDir);
            File siteFile            =   this.getDefaultSiteFile(configFileStorageDir);
            
            IO.writeFileFromString(this.envvarsContent, envvarsFile );
            IO.writeFileFromString(this.portsContent, portConfigFile);
            IO.writeFileFromString(this.siteContent, siteFile );
            
       }
       catch (Exception e){
           out                  =   false;
           configFileWriteError       =   e.getMessage();
           e.printStackTrace();
       }
       finally{
           return out;
       }
      
       
   
   }
   public boolean writeConfiguratioFiles (File dir){
       boolean out  =   false;
       try{
           configFileStorageDir =   dir;
           out                  =   writeConfiguratioFiles ();
       }
       catch (Exception e){
           e.printStackTrace();
       }
       finally{
           return out;
       }
      
       
   
   }
   public String  getSudoScriptToCompleteInstallation( File   linkFile ){
       StringBuilder sb             =   new StringBuilder();
       File   startstopFile         =   this.getApache2StartStopFile();
       File   srcEnvvarsFile        =   this.getEnvvarsFile(configFileStorageDir);
       File   dstEnvvarsFile        =   this.getEnvvarsFile();
       File   srcPortConfFile       =   this.getPortConfFile(configFileStorageDir);
       File   dstPortConfFile       =   this.getPortConfFile();
       File   srcDefaultSiteFile    =   this.getDefaultSiteFile(configFileStorageDir);
       File   dstDefaultSiteFile    =   this.getDefaultSiteFile();
       // stop apache server if it is already running
       sb.append("sudo "+startstopFile.getPath()+ " stop");
       sb.append("\n");

       // unalias cp
       sb.append("sudo unalias cp" );
       sb.append("\n");
       
       // copy configuratio files
       sb.append("sudo cp "+ srcEnvvarsFile.getPath() + " "+ dstEnvvarsFile.getAbsolutePath() );
       sb.append("\n");
       sb.append("sudo cp "+ srcPortConfFile .getPath() + " "+ dstPortConfFile .getAbsolutePath() );
       sb.append("\n");
       sb.append("sudo cp "+ srcDefaultSiteFile.getPath() + " "+ dstDefaultSiteFile.getAbsolutePath() );
       sb.append("\n");
       
       File docRoot                     =    generateDocumentRoot();
       // makse sure document root exists
       if (docRoot.exists() ==  false){
           sb.append("sudo mkdir -p "+docRoot.getPath() + "\n");
       }
       //create link to bayes directory
        sb.append("cd "+ docRoot.getPath() );
        sb.append("\n");
        sb.append("sudo rm -f "+ FileManager.BAYES);
        sb.append("\n");

        String dir          =   FileManager.getBayesDir().getAbsolutePath();
        sb.append("sudo ln -s "+ dir);
        sb.append("\n");
       

       sb.append("sudo "+startstopFile.getPath() + " start");
       sb.append("\n");


        return sb.toString();

   }
   
   public String getPath(){
       if (this.apache2Dir != null){return apache2Dir.getPath();}
       else {return "";}
   }
   public static void main (String args []){
      Apache2 ap2   = new  Apache2 ();
      File root     =   new File ("/Users/apple/Desktop/apache2/");
      ap2.setApache2Dir(root);
     // boolean isGood = ap2.checkApache2InstanceIntegrity();
     //  System.out.println("Valid apache2 dir? "+isGood);
       
     File envvars           =   new File ("/Users/apple/Desktop/apache2/envvars");
     //ap2.updateEnvvarsContent(envvars );
     // System.out.println(ap2. envvarsContent );
     
      File ports           =   new File ("/Users/apple/Desktop/apache2/ports.conf");
    ap2.updatePortsConfContent(ports );
     //  System.out.println(ap2.portsContent);
   
       File site = new File ("/Users/apple/Desktop/apache2/sites-available/default");
       ap2.updateSiteContent(site);
       System.out.println(ap2.siteContent);
   }

    public File getApache2Dir() {
        return apache2Dir;
    }
    public void setApache2Dir(File apache2Dir) {
        this.apache2Dir = apache2Dir;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }

    public String getInegrityCheckError() {
        return inegrityCheckError;
    }
    public void setInegrityCheckError(String inegrityCheckError) {
        this.inegrityCheckError = inegrityCheckError;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getScriptAlias() {
        return scriptAlias;
    }

    public void setScriptAlias(String scriptAlias) {
        this.scriptAlias = scriptAlias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public File getConfigFileStorageDir() {
        return configFileStorageDir;
    }

    public void setConfigFileStorageDir(File configFileStorageDir) {
        this.configFileStorageDir = configFileStorageDir;
    }

    public String getConfigFileWriteError() {
        return configFileWriteError;
    }
   
}
