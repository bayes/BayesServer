/*
 * RunCommand.java
 *
 * Created on May 22, 2007, 10:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author apple
 */
package utilities;
import java.io.*;

public class RunCommand {

   public static String execute(String str){
          String [] comandandargs  = str.trim().split("\\s");
          return execute(comandandargs);
    }
   public static String execute(String [] str ){
            StringBuffer sb = new StringBuffer();
            String s;
            Process p                  =    null;
            try {
                ProcessBuilder pb      =  new ProcessBuilder(str);
                pb.redirectErrorStream(true);
                p                       =   pb.start();
                InputStreamReader isr   = new  InputStreamReader(p.getInputStream());
                BufferedReader stdInput = new BufferedReader(isr);


                /*
                System.out.println("Input to native command:");
                for (String string : str) {
                    System.out.print(" "+string);
                }
                System.out.println("");
                */


                while (( s = stdInput.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");

                    System.out.println("Native command intermidiate output "+ s);
                }

           }
         
            catch (IOException ex) {
                        ex.printStackTrace();
            }
            finally{
                try{
                    p.waitFor();//here as there is some snipped code that was causing a different
                    // exception which stopped it from getting processed

                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();

                  } catch (Exception ioe) {ioe.printStackTrace();}

            }
            return sb.toString();
    }


    public static void main(String args[]) {

            String s        = null;
            String str;
            String result   = "";
            String [] com;
            
            if (args != null) {

               // str = args[1];
                //com = new String []{"tar", "-xvf", str};
                com = new String []{"env"};

                //result = execute(str);
                 result = execute(com);
                System.out.println( result);


         int ind = result.indexOf("SHELL");
        if(ind < 0){return;}

        ind     =   result.indexOf("=", ind);
        if(ind < 0){return;}

        int indEnd = result.indexOf("\n", ind);
        if(indEnd < 0){return;}

        String shell = result.substring(ind +1, indEnd).trim();

                System.out.println(shell);
            }
           // File file  = new File ("/Users/apple/system/tar.tar");
             //com = new String []{"tar", "-xvf", file.getAbsolutePath()};
           //  result = execute(com);
            //  System.out.println( result);
    }
}