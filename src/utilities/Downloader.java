/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.io.*;
import java.net.*;

/**
 *
 * @author apple
 */
public class Downloader {
      public static int connectionTimeOut           = 1000;     // in millisecond
      public static int readTimeOut                 = 1000*3; // in millisecond
      public static int  contentLength              = 0;
      public static int  contentWritten             = 0;
    public static boolean downloadFile (  String remoteFile, File localFile)throws SocketTimeoutException{
      boolean isDownloaded          =   false;
      URL  url                      =   null;
      OutputStream out              =   null;
      HttpURLConnection conn        =   null;
      InputStream  in               =   null;
      contentLength                 =   0;
      contentWritten                =   0;


      try {
            url                     =   new URL(remoteFile);
            out                     =   new BufferedOutputStream(new FileOutputStream(localFile));
            conn                    =   (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(connectionTimeOut);
            conn.setReadTimeout(readTimeOut);

            //System.out.println("Connection response code = "+conn.getResponseCode());

            contentLength         =   conn.getContentLength();
            System.out.println("Download size "+ contentLength );
            in                      =   conn.getInputStream();
            byte[] buffer           =   new byte[1024];

            int numRead;
            while ((numRead     = in.read(buffer)) != -1) {

                    out.write(buffer, 0, numRead);
                    contentWritten  += numRead;
            }
            isDownloaded = true;
            }
            catch (SocketTimeoutException exception) {
                exception.printStackTrace();
                 throw new SocketTimeoutException( exception.getMessage());
	    } catch (Exception exception) {
                isDownloaded = false;
                exception.printStackTrace();


            } finally {
                try {
                    if (in != null) {in.close(); }
                    if (out != null) {out.close();}
                   
                } catch (IOException ioe) {}

                return isDownloaded ;
            }
     }


public static void main(String args[]){
      try {
            String remoteFile   = "http://bayes.wustl.edu/ServerSoftware/bayes.1.0.sun4.tar.gz";
            String url          = "http://bayes.wustl.edu:8080/";
             String dir          = "../ServerSoftware";

          //   url                ="http://bayes.wustl.edu:8080/";
          //   dir          = "Bayes.Predefined.Spec";

          //  List<String>  fileList =   getFileListing(url, dir, "apple",null);
            File   toFile       = new java.io.File("testaaa");
            boolean isCreated  = toFile.createNewFile();

         // for (String string : fileList) {  System.out.println(string); }
            
         
           Downloader.downloadFile(remoteFile,toFile);
        }
     
        catch (Exception ex) {
             ex.printStackTrace();
        }
}
 
}








class MyAuthenticator extends Authenticator {
        String username;
        String password;

        MyAuthenticator(String username, String password){
             this.username = username;
             this.password = password;
        }
        // This method is called when a password-protected URL is accessed
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {


            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
    class FailedAuthentificationException extends Exception {
}