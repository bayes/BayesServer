import java.io.*;


public class RunCommand { 

   public static String execute(String str){
          String [] comandandargs  = str.trim().split("\\s");
          return execute(comandandargs);
    }
   public static String execute(String [] str ){
            StringBuffer sb = new StringBuffer();
            String s;
            try {
                Process p                = new ProcessBuilder(str).start();
                BufferedReader stdInput = new BufferedReader(new
                                                InputStreamReader(p.getInputStream()));

                while (( s = stdInput.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }

               // System.out.println(result);
                int exitVal             = p.waitFor();
           }
            catch (InterruptedException ex) {
                     ex.printStackTrace();
            }
            catch (IOException ex) {
                        ex.printStackTrace();
            }
            finally{
            }

            return sb.toString();
    }


    public static void main(String args[]) {

            String s        = null;
            String str;
            String result   = "";
            String [] com;

            if (args != null) {
                // System.out.println("Arguments"); ;
               // for (String string : args) {
                //   System.out.println(string );
               // }
                //System.out.println("");

                //com = new String []{"tar", "-xvf", args[0]};
                result = RunCommand.execute(args);
                System.out.println( result);
            }
           // File file  = new File ("/Users/apple/system/tar.tar");
             //com = new String []{"tar", "-xvf", file.getAbsolutePath()};
           //  result = execute(com);
            //  System.out.println( result);
    }
}
