/*
 * FileCompress.java
 *
 * Created on July 20, 2007, 10:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utilities;

import java.io.*;
import java.util.zip.*;



/**
 *
 * @author apple
 */
public class FileCompress {
    
    /** Creates a new instance of FileCompress */
    public FileCompress () {
    }
    public static void gzip(String inFilename, String outFilename){
         try {
            // Create the GZIP output stream
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFilename));

            // Open the input file
            FileInputStream in = new FileInputStream(inFilename);

            // Transfer bytes from the input file to the GZIP output stream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();

            // Complete the GZIP file
            out.finish();
            out.close();
        } catch (IOException e) {}
   
    }
    public static void unGzip(String inFilename, String outFilename){
         try {
            // Create the GZIP input stream
           GZIPInputStream in = new GZIPInputStream(new FileInputStream(inFilename));

           // Open the input file
            FileOutputStream out = new FileOutputStream(outFilename);

            // Transfer bytes from the  GZIP output to the input file stream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
    }
    
    public static long chksum(String filename){
        long checksum = 0;
        try {
            // Compute CRC-32 checksum
            // this checksum is equivalent to "BSD UNIX cksum -o 3" command
            CheckedInputStream cis = new CheckedInputStream(
                       new FileInputStream(filename), new CRC32());
             /*      
            // Compute  Adler-32 checksum
            CheckedInputStream cis = new CheckedInputStream(
                        new FileInputStream(filename), new Adler32());
             */ 
            byte[] tempBuf = new byte[1024];
            while (cis.read(tempBuf) >= 0) {}
            checksum = cis.getChecksum().getValue();
            
        } catch (IOException e) {}
        //System.out.println("checksum = "+checksum);
       return checksum;
    }
    public static long chksum(File file){
       return  chksum(file.getAbsolutePath());
    }
    
    public static void writeChksumFile(String chksumFile, String fileName ){
        String text;
        File file = new File (fileName);
        if (file == null){
            System.err.println("No file "+ fileName + "exist");
            System.err.println("Exiting...");
            System.exit(1);
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter( chksumFile));
            out.write(String.valueOf(FileCompress.chksum(fileName)) + "      ");
            out.write(String.valueOf(file.length ())+"    ");
            out.write(file.getName ());
            out.newLine();
            out.newLine();
            out.close(); 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main (String [] args){
        int magic = GZIPInputStream.GZIP_MAGIC;
          //System.out.println("" +  GZIPInputStream.GZIP_MAGIC);
           //System.out.println("GZIP_MAGIC = "+ Integer.toBinaryString(magic));
           unGzip("/Users/apple/Bayes/job.tar.gz", "test_unzip");
    
    }
}
