/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import java.io.*;
/**
 *
 * @author apple
 */
public class HTAccessFile {
     
      public static final String  EOL;
      static{
        String str = System.getProperty("line.separator");
        EOL        = (str == null)? "\n": str;

      }

         public static String createContent(File destinationFile){
            StringBuilder sb = new StringBuilder();
            sb.append("AuthName \"Job Submission\"");
            sb.append(EOL);

            sb.append("AuthType Basic");
            sb.append(EOL);

            String line =  "AuthUserFile " +  " "+destinationFile.getAbsolutePath();
            sb.append(line);
            sb.append(EOL);

            // blank line
            sb.append(EOL);

            sb.append("<LIMIT GET POST PUT>");
            sb.append(EOL);

            sb.append("require valid-user");
            sb.append(EOL);

            sb.append("</LIMIT>");
            sb.append(EOL);


            return sb.toString();

        }


}
