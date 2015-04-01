/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import utilities.Downloader;
import utilities.DisplayText;


/**
 *
 * @author apple
 */
public class URLManager implements Constants{
    public static final String BAYES_SOURCE_SERVER           = "bayes.wustl.edu";
    public static final String INSTALLATION_KIT_URL_DIR      = "ServerSoftware";
    public static final String INSTALLATIONTEST_KIT_URL_DIR  = "ServerSoftwareTest";
    public static final String DISTRIBUTION_LISTING_FILENAME = "DistributionKitListing";
    public static final String REGISTER_CGI_SCRIPT           = "cgi-bin/Bayes_get_info";
    public static final int    MASTER_SERVER_CGI_PORT        =  8080;

    public static String getMasterServerURL(){
         String url          =   "http://"+ BAYES_SOURCE_SERVER+ "/";
         return url;
    }
    public static String getMasterServerURL(int port){
         String url          =   "http://"+ BAYES_SOURCE_SERVER+":"+port+ "/";
         return url;
    }
    public static String getMasterServerInstallKitURL(){
         String url          =   getMasterServerURL()+INSTALLATION_KIT_URL_DIR+ "/" ;
       // String url          =   getMasterServerURL()+ INSTALLATIONTEST_KIT_URL_DIR+ "/" ;
         return url;
    }
   
     public static String getInstallationKitURL(String installationKitName){
         String src          =   getMasterServerInstallKitURL()+installationKitName ;
         return src ;
    }
     public static String getInstallationKitListingURL(){
         return getMasterServerInstallKitURL() + DISTRIBUTION_LISTING_FILENAME;
    }

     public static  List<String> getInstallationList() throws IOException {
         List<String> files     =    new ArrayList<String>();
         String url             =    getInstallationKitListingURL();
         File tmp               =    new File ("tmp");
         boolean isCreated      =   tmp.createNewFile();

         if (isCreated  == false) {return files;}
         try{
             boolean isDone     =    Downloader.downloadFile(url, tmp);
             Scanner scanner    =    new Scanner(tmp);
             while(scanner.hasNextLine()){
               files.add(scanner.nextLine().trim());
             }
         }
       
         finally { 
             tmp.delete();
         }
           
         

         return files ;
    }
     public static  String getInstallationKitName(PLATFORM platform, boolean is64Bit){
        String installKitFile   =   null;
        List<String> files      =   getInstallationKitNames();
        String errorMessage     =   null;

        for (String string : files) {
            String str          =   platform.getName();
            if (is64Bit){str    =   str + "64";}
            if (string.contains(str )){
                installKitFile = string;
                break;
            }
        }

        // if not found
        if (installKitFile  == null){
            errorMessage = String.format("No installation kit is found\n" +
                "for opertating system %s",platform);
            DisplayText.popupMessage(errorMessage);
        }

         

       return installKitFile ;
            
        
    }
     public static  List<String> getInstallationKitNames(){
        List<String> files      =   new  ArrayList<String>();
        String errorMessage     =   null;

        try{
            files                   =    getInstallationList();
        }
        catch(IOException exp){
                exp.printStackTrace();
                errorMessage = String.format("Failed to retrieve installatio kit listing.\n" +
                      "Error message:\n%s "  , exp.getMessage());
               DisplayText.popupMessage(errorMessage);
         }


       return  files ;
            
        
    }
     public static  String getInstallationVersion(){
        String version              =   null;
        try{
            String installKitName       =   getInstallationKitNames().get(0);

            String spr                  =   ".";
            int start                   =    installKitName.indexOf(spr );
            int floatPoint              =    installKitName.indexOf(spr, start+1 );
            int end                     =    installKitName.indexOf(spr, floatPoint+1);


            if (start < 0 || end <= start){return null;}
            else{version         =   installKitName.substring(start+1, end);}
        } catch (Exception e){e.printStackTrace();}
        

        return version;

    }
     
  public static boolean           connectToURL(String urlStr)
    throws UnknownHostException, ConnectException, ConnectException, MalformedURLException, IOException{
      boolean success                           =   false;

             URL  url                           =   new URL(urlStr);

             System.out.println("connecting to url " + url);
             HttpURLConnection connection       = (HttpURLConnection) url.openConnection();
             connection.setConnectTimeout(500);
             connection.setReadTimeout(500);
             connection.setDoOutput(true);
             connection.setDoInput(true);

             int response = connection.getResponseCode();
             if (response == 200){success  = true;}

             System.out.println("ConnectToURL " +urlStr+" "+ response);
                 

     return success  ;


 }


     public static void main (String [] args){
       System.out.println( URLManager.getInstallationVersion());

//
       //  System.out.println( getInstallationKitName(PLATFORM.SUN));
         
         /*
         java.util.Enumeration enumer = javax.swing.UIManager.getDefaults().keys();


    while(enumer.hasMoreElements())
    {
      Object key = enumer.nextElement();
      Object value = javax.swing.UIManager.get(key);
      if (key.toString().contains("Tip")){
         System.out.println(key + " =  "+ value);
        }
      if (key.toString().contains("margin")){
         System.out.println(key + " =  "+ value);
        }
      }
        */
     }
}
