/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;

import java.io.File;
import java.util.Properties;

public class InstallerWIthApache2 extends BaseInstaller{
        
 private  Apache2 apache2                =   new Apache2();
 private final String APACHE2_DIR_KEY    =   "APACHE2 DIR";
 private void setupApache2(){
    apache2.setPort(port);
    apache2.setGroup(group);
    apache2.setEmail(email);
    apache2.setScriptAlias(FileManager. getCgiBinDir().getAbsolutePath());
    apache2.setConfigFileStorageDir(FileManager.getSystemDir());
 }

 @Override  
 public boolean isReadyToInstallApache(){
        return true;
    }
 
 @Override
 public String    getInstallationCompeteMessage(){
      if (this.skipApacheSetup){return "";}
      return apache2.getSudoScriptToCompleteInstallation(virginConfFile);
}
 
 @Override
 public void updateAndStoreInstallationProperties(){
    Properties p      =   this.properties;
    if (apache2.isValid()){
        setProperty(p, APACHE2_DIR_KEY , apache2.getApache2Dir().getAbsolutePath());
    }
    writeProperties();
}

  
  
@Override
public void apacheSetup(String errormessage) throws Exception{
   setProgressMessage("Write apache2 configuration files");
    this.setupApache2();
    boolean apache2IsGood= this.apache2.writeConfiguratioFiles ();
    if (apache2IsGood == false){
         errormessage     =    apache2.getConfigFileWriteError();
         setProgressMessage( errormessage);
         throw new Exception (errormessage );
    }
}
     
@Override
public void readFromInstallationProperties(){
    readProperties();
    Properties p      =   this.properties;

    File dir            =  getDirectoryFromProperty(p,APACHE2_DIR_KEY  );
    if (dir != null){
        Apache2  ap    = new  Apache2(dir);
        if (ap.checkApache2InstanceIntegrity() == true){
            this.apache2 = ap;
        }

    }
  }
}

