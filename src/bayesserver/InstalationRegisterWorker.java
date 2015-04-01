/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import javax.swing.SwingWorker;
import utilities.IO;


public class InstalationRegisterWorker extends SwingWorker<Void, Void>{
 public final static int readTimeout            =   1800*1000;
 public final static int connectionTimeout      =   3*1000;
 public static final String dummyExtraCaracter   =   ".";
 public String serverUrl                       =   null;
 public String scriptPath                      =   null;
 public String request                         =  null;


     @Override
     public Void doInBackground() {
            submit ();

            return null;

     }

      public void submit () {
        PrintWriter out                 =   null;
        BufferedReader in               =   null;
        HttpURLConnection connection    =   null;
        String         urlString        =  serverUrl   + scriptPath  ;
        URL  url                        =   null;
        int code                        =   0;
        System.out.println("Register Installation at "+ serverUrl );
        System.out.println("Full URL:"+ urlString);
         System.out.println("Request: "+request  );
         try{
             url                        =   new URL(urlString);
             connection                 =   (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("POST");
             connection.setDoOutput(true);
             connection.setConnectTimeout(connectionTimeout);
             connection.setReadTimeout(readTimeout);
        
              out                        =   new PrintWriter(connection.getOutputStream());

              out.println ("");
              out.println ("");
              out.println ("");
              out.println(request+ dummyExtraCaracter);
              out.flush ();

            code = connection.getResponseCode();
            
            
           

     } catch (Exception e) {
               e.printStackTrace();

     } finally {
            try {
                if (in != null) { in.close();}
                if (out!= null) { out.close();}
                
                StringBuilder str = new StringBuilder();
                str.append("Register Installation at "+ serverUrl );
                str.append("\n");
                str.append("Full URL:"+ urlString );
                str.append("\n");
                str.append("Request: "+request  );
                str.append("\n");
                str.append("Connection response: "+code  );
                str.append("\n");
                
                writeDebugFile(str.toString());
                
            } catch (IOException ex) {}
     }
 }
      
     public boolean writeDebugFile(String content){
      File dir                  =   new File ("/tmp");
      Date date                 =   new Date();
      String name               =   "installDebugBayesInstalReg_"+ date+date.getTime();
      File  file                =    new File (dir,name);
       
        boolean isSuccess       =   IO.writeFileFromString( content, file );
        return true;
    }

}