/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesserver;

import bayesserver.Constants.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import utilities.RunCommand;

/**
 *
 * @author apple
 */
public class SystemInfo {
    public  String osarch                   =   null;
    public  String osname                   =   null;
    public  String osrelease                =   null;
    public  PLATFORM platform               =   PLATFORM.UNKNOWN;
    public  String state                    =   "undefined";
    
    {
        state                    =   "undefined";
        try{
            getInfo();
            state = "defined";
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    
    public boolean isDefined(){
      return  state.equalsIgnoreCase("defined");
    }
    public void getInfo(){
        
        osname =   System.getProperty(Constants.OS_NAME);
        
        osarch =   RunCommand.execute("arch");
        if (osarch == null || osarch.length() < 1){
             osarch                =   RunCommand.execute(new String [] {"uname", "-m"});
        }
        
        if (osname.contains("Sun") || osname.contains("sun")){
           platform = PLATFORM.SUN;
        }
        else if(osname.contains("linux") ||  osname.contains("Linux")){
             platform = PLATFORM.LINUX;
        }
        
        String osreleaseHOLD                        =   osrelease;
        switch (platform){
            case SUN :
                         osreleaseHOLD              =   findReleaseSun();
                         break;
            case LINUX :
                         osreleaseHOLD              =   findReleaseLinux();
                         break;
         }
         osrelease                                  =   osreleaseHOLD;
        
    }

public boolean isApache2Installed (){
   //TODO setting default false for no
    return false;
}
public boolean isUbuntu (){
    boolean out     =    false;
    if (osrelease != null){
       out =  osrelease.contains(PLATFORM.UBUNTU);
    }
    return out;
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
}


   