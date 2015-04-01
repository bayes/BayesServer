
//
//  IO.java
//  DoStats
//
//  Created by karenmarutyan on 12/1/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//
/**************************************************************
* This code is intended to create methodes for Input/Output files
* with underlying operating system.
*
***************************************************************/
package utilities;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;


public class IO {
public final static String WHITE_SPACE = "\\s+";
		
	
  //***************************************************************************//
  //* in this code Sting.split(String regex) method introduced in java 5.0 is used
  //* to read data from ASCII FILE
  //* ATTENTION: indexing start from 1!!!!!!
  public static double[] nASCI2double(File file, int k, String regex) throws FileNotFoundException{
        ArrayList <String> array    = new  ArrayList<String>();
        Scanner scanner             = new Scanner(file);
       
        while (scanner.hasNextLine()){
                    String [] strs = scanner.nextLine().trim().split (regex);
                    array.add( strs[k-1]);
        }

       scanner.close();
       double [] data = new double [array.size()];
       for (int i = 0; i < data.length; i++){
           data[i] = Double.valueOf(array.get (i));
       }
        
     return data;
  } 
  public static double[] nASCI2double(String  filename, int k, String regex)throws  FileNotFoundException{
      File file = new File (filename);
      return  nASCI2double(file, k, regex);
  }
  public static double[] nASCI2double(File file, int k) throws FileNotFoundException, IOException{
     return nASCI2double(file, k, WHITE_SPACE);
  } 
  public static double[] nASCI2double(String filename, int k)throws FileNotFoundException, IOException{
     return nASCI2double(filename, k, WHITE_SPACE);
  } 
  
  /***************************************************************************/
  /* This function parses multicolumn white text delimited  file to string list     */
  
  
    public static List <String>  ASCI2String(File file) throws FileNotFoundException{
        String content              =   readFileToString(file);
        Scanner scanner             =   new Scanner(content);
        List <String> data          =   new ArrayList <String> () ;
        
        while (scanner.hasNextLine()){
            data.add(scanner.nextLine());   
        }
       
        scanner.close();
     
       return data;
  } 

  /***************************************************************************/
  
  
  public static void     dbl2ASCII(double [] x, double [] y , File file, String regex ) throws IOException{
        BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));
        String line;
            for (   int i = 0 ; i < x.length	;i++){
                line  = x[i] + regex + y[i];
                out.write(line);
                out.newLine();
            }
        out.close();
    }
  public static void     dbl2ASCII(double [][] data , File file, String regex ) throws IOException{
        BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));
       
        for (int i = 0; i < data.length; i++) {
             String line = "";
            for (   int j = 0 ; j < data[0].length	;j++){
                line  = line+ data [i][j] + regex;
            }
            out.write(line);
            out.newLine();
       }
   
        out.close();
 }
  public  static void float2ASCII(float [][] data , File file, String regex , String format) throws IOException{
       StringBuffer sb         =    new StringBuffer();
       int numberOfCoumns      =    data.length;
       int numberOfRows        =    data[0].length;
       
       for (   int i = 0 ; i < numberOfRows	;i++){
            for (int j = 0; j < numberOfCoumns ; j++) {
                sb.append(String.format(format, data[j][i] ));
                sb.append(regex);
            }
            sb.append("\n");         
        }
    
      
        utilities.IO.writeFileFromString(sb.toString(), file); 
    }
  public  static void writeFile( float [][] abscissa, float[][] data,  int numElem, File file    ){
        FileWriter fr           = null;
        BufferedWriter out      = null;
        String frmt             = "%.7E";
        
         try {
            fr = new FileWriter(file);
            out = new BufferedWriter(fr);

            for (int i = 0; i < numElem; i++) {
                String str = new String();
                if (abscissa != null){
                    for (int j = 0; j < abscissa.length; j++) {
                         str = str + String.format("  "+ frmt,abscissa [j][i]);
                    }
                }
                 for (int j = 0; j < data.length; j++) {
                     str = str + String.format("  "+ frmt,data[j][i]);
                 }
                
                out.write(str);
                out.newLine();
            }
           
        } catch (Exception ex) {
              ex.printStackTrace();
        } 
        finally
        {
            try {
                out.close();
                fr.close();
            } catch (IOException ex) {
            }
               
        }
}   
  public  static void writeFile( float [][] abscissa, double[][] data,  int numElem, File file    ){
        FileWriter fr           = null;
        BufferedWriter out      = null;
        String frmt             = "%.7E";
        
         try {
            fr = new FileWriter(file);
            out = new BufferedWriter(fr);

            for (int i = 0; i < numElem; i++) {
                String str = new String();
                 for (int j = 0; j < abscissa.length; j++) {
                        str = str + String.format("  "+ frmt,abscissa [j][i]);
                 }
                 for (int j = 0; j < data.length; j++) {
                     str = str + String.format("  "+ frmt,data[j][i]);
                 }
                
                out.write(str);
                out.newLine();
            }
           
        } catch (Exception ex) {
              ex.printStackTrace();
        } 
        finally
        {
            try {
                out.close();
                fr.close();
            } catch (IOException ex) {
            }
               
        }
}   


/***************************************************************************/
/* Reads an ASCI TExt File to a StringBuilder
/***************************************************************************/    
 
    public static StringBuilder ASCII2StringBuilder(String str) throws IOException{
    return ASCII2StringBuilder(new File(str));
}    
    public static StringBuilder ASCII2StringBuilder(File file) throws IOException{
    StringBuilder sb            =   new StringBuilder();
    Scanner scanner             =   new Scanner(file);
            
    while ( scanner.hasNextLine()){sb.append (scanner.nextLine());}
    scanner.close();
    
    return sb;
}  

//***************************************************************************/
/* Counts number of line and columns in the text file
/***************************************************************************/  
  public static int getNumberOfLines(String filename)throws FileNotFoundException, IOException{
      File file                   =   new File(filename);
      return getNumberOfLines(file);
  }
  public static int getNumberOfLines(File file)
    throws FileNotFoundException, IOException
  {
        int count                       =   0;
        FileReader fileRead             =   null;
        LineNumberReader lineRead       =   null;

        try {
            long lastRec                =   file.length();

            fileRead                    =   new FileReader(file);
            lineRead                    =   new LineNumberReader(fileRead);
            lineRead.skip(lastRec);
            count                       =   lineRead.getLineNumber();

        }

      finally{
            try {
                fileRead.close();
                lineRead.close();
            } catch (IOException ex) { ex.printStackTrace();}
           
        }
      return count;
  }


  public static int getNumberOfColumns(File file)throws IOException{
                              
        Scanner scanner             = new Scanner(file);
        String    line              = scanner.nextLine();
        scanner.close();
       
        return line.trim().split("\\s+").length ;
    }
  public static void getCurrentDir () {
     File dir1 = new File (".");
     File dir2 = new File ("..");
     try {
       System.out.println ("Current dir : " + dir1.getCanonicalPath());
       System.out.println ("Parent  dir : " + dir2.getCanonicalPath());
       }
     catch(Exception e) {
       e.printStackTrace();
       }
     }
    
//************************************************************************/
/* Deletes file with the specified name.
/************************************************************************/    
   public static void deleteFile(String fileName) {
    File f = new File(fileName);
    
    if (!f.exists()){
      throw new IllegalArgumentException(
          "deleteFile: no such file or directory: " + fileName);
    }

    if (!f.canWrite()){
      throw new IllegalArgumentException("deleteFile: write protected: "
          + fileName);
    }
    // If it is a directory, make sure it is empty
    if (f.isDirectory()) {
      IO.emptyDirectory(fileName);
    }
    
    boolean success = f.delete();

    if (!success){
      throw new IllegalArgumentException("deleteFile: deletion failed");
    }
  }
    
//************************************************************************/
//* Deletes all files in the specified directory.
//************************************************************************/   
  public static void emptyDirectory(String dirName) {
    
    File dir = new File(dirName);
    emptyDirectory (dir);
  } 
  public static void emptyDirectory(File dir) {
    String dirName = dir.getPath();
   
    if (!dir.exists()) {
      System.out.println(dirName + " does not exist");
      return;
    }

    String[] info = dir.list();
    for (int i = 0; i < info.length; i++) {
      File n = new File(dirName + dir.separator + info[i]);
      if (!n.isFile()) {continue;}  // skip ., .., other directories too
      if (!n.delete()) { System.err.println("Couldn't remove " + n.getPath());}
    }
  }  
 //************************************************************************/
//* Deletes all files including other directories  in the specified directory.
//************************************************************************ 
   static public void deleteFielsAndDirsInDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    return ;
  }
   static public boolean deleteDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    return( path.delete() );
  }
   static public boolean deleteDirectory(String path) {
    
    return  deleteDirectory(new File(path));
  }
  //****************************************************************//
  //* copies one file to another
  //* If the destination file does not exist, it is created
  //****************************************************************//
  public static  boolean copyFile(File src, File dst) {
      try {
            // do nothing if the source file is a destination file.
            if (src.getAbsoluteFile().equals(dst.getAbsoluteFile())) {
                return false;
            }

            File parentDir  =   dst.getParentFile();
            if ( parentDir.exists() == false){
                 parentDir.mkdirs();
            }

            // if source and destinations files are different 
            InputStream in      =   new FileInputStream(src);
            OutputStream out    =   new FileOutputStream(dst);
   
        // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            
            in.close();
            out.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
       return true;
    }
  public static  boolean copy(InputStream in, File dst) {
      try {

            File dir = dst.getParentFile();
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            // if source and destinations files are different
            OutputStream out    =   new FileOutputStream(dst);

        // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();

            in.close();
            out.close();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
       return true;
    }
  //****************************************************************//
  //* Copies all files under srcDir to dstDir.
  //*  If dstDir does not exist, it will be created.
  //****************************************************************//
  public static  boolean copyDirectory(File srcDir, File dstDir) {
        // do nothing if the source file is a destination file.
        if (srcDir.getAbsolutePath().equals(dstDir.getAbsolutePath())) {
            return false;
        }

        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {dstDir.mkdir();}

             String[] children = srcDir.list();
             for (int i=0; i<children.length; i++) {
                 copyDirectory(new File(srcDir, children[i]),
                                 new File(dstDir, children[i]));
        }
        } else {
             copyFile(srcDir, dstDir);
        }
      return true;
    }
  public static  boolean copyDirectory(String srcDir, String dstDir) {
        File src = new File (srcDir);
        File dst = new File (dstDir);
        
        return copyDirectory(src, dst);
    }
  
  
  
  /************************************************************
   *
   *  Read Ascii file into the String
   * @param file  - file to read from
   * @return  String which content is identical to content of the input file
   *
   *********************************************************************/
   public static  String readFileToString(File file) {
        FileInputStream fis = null;
        String content      = null;
        int fileSizeInBytes = 0;
        if (file == null || file.exists() == false || file.isFile() == false) {
            return content;
        }
        try {
            fis                 = new FileInputStream(file);
            fileSizeInBytes     = fis.available();
            byte[] buffer       = new byte[fileSizeInBytes];
          
            fis.read(buffer);
            content = new String(buffer);
            return content;
            
        } catch (FileNotFoundException ex) {
              ex.printStackTrace();
        } catch (IOException ex) {
              ex.printStackTrace();
        } finally {
            try {
                fis.close();
                return content;
            } catch (IOException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
                return content;
            }
        }
    }

    public static  String readInputStreamToString(InputStream is) {
        BufferedInputStream bis = null;
        String content      = null;
        int fileSizeInBytes = 0;
        if (is == null ) {return content;}
        try {
            bis                 = new  BufferedInputStream(is);
            fileSizeInBytes     = bis.available();
            byte[] buffer       = new byte[fileSizeInBytes];

            bis.read(buffer);
            content = new String(buffer);
            return content;

        } catch (FileNotFoundException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bis.close();
                is.close();
                return content;
            } catch (IOException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
                return content;
            }
        }
    }

     /************************************************************
   * 
   * @param file  - file to read from
   * @return  String which content is identical to content of the input file
   *********************************************************************/
   public static  boolean writeFileFromString(String content, File file)  {
        FileWriter fr   =   null ;
        File parenDir   =   file.getParentFile();

        if (parenDir != null &&parenDir.exists() == false){
           parenDir.mkdirs();
        }
        try {
            fr    =    new FileWriter (file);
            fr.write(content);
            return true;
           
        } catch (FileNotFoundException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
                
                return false;
        } catch (IOException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
                 return false;
        } finally {
            try {
                if (fr!= null) {fr.close();}
                
            } catch (IOException ex) {
                Logger.getLogger(IO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

   //****************************************************************//
   //* Pads out a string up to padlen with pad chars
   //* @param Object.toString() to be padded
   //* @param length of pad (+ve = pad on right, -ve pad on left)
   //* @param pad character
   //****************************************************************//
      
    public static String pad(Object str, int padlen, String pad){
        String padding = new String();
        int len = Math.abs(padlen) - str.toString().length();
        if (len < 1)
        return str.toString();
        for (int i = 0 ; i < len ; ++i){
             padding = padding + pad;
        }
      
        return (padlen < 0 ? padding + str : str + padding);
    }
    
    //****************************************************************//
   //* Pads out a string up to padlen with pad chars with count relative to 
   //* character ch within the string
   //* @param Object.toString() to be padded
   //* @param length of pad (+ve = pad on right, -ve pad on left)
   //* @param pad character
   //* @param padding reference character within the string
   //****************************************************************//
      
    public static String padToChar(Object obj, int padlen, String pad, char ch){
    String padding  =   new String();
    String str      =   obj.toString();
    int ind         =   str.indexOf(ch);
    int len         =   Math.abs(padlen) - (ind);
    if (len < 1){
      return str.toString();
    }
    for (int i = 0 ; i < len ; ++i){
      padding = padding + pad;
    } 
    return (padlen < 0 ? padding + str : str + padding);
    }
    
    
   
    
       //****************************************************************//
   //* Pads out a string up to padlen with pad chars
   //* @param Object.toString() to be padded
   //* @param fronPad  - length of pad to be inserted on the left
   //* @param padLen   -  minimal length of the final string. If necesary the pads are inserted on the right.
   //* @param pad character
   //****************************************************************//
      
    public static String pad(Object str, int fronPad, int padLen, String pad){
        StringBuilder padding = new StringBuilder (str.toString());
      
        for (int i = 0 ; i < Math.abs(fronPad) ; ++i){
             padding = padding.insert(0,pad);
        }
        return  pad(padding, padLen, pad);
    }
    
    
   //****************************************************************//
   //*Find file extension
   //* @param File wich extension should be found
   //* 
   //****************************************************************//
   public static String           getExtension(File file){
        String name             = file.getName();
        int ind                 = name.lastIndexOf(".");
        String ext              = name.substring(ind + 1, name.length());
        return ext;
     } 
    
  //****************************************************************//
   //* Copies files and replaces (if applicable) the end of line character
   //* from default Windows "\r\n" to default Unix "\n".
   //
   //* @param Source file to be copied. 
   //* It assumed to have  Windwos ("\r\n")or Unix End Of Line character ("\n")
   //*
   //* @param Destination file with Unix End Of Line character ("\n").
   //****************************************************************//
  public static boolean win2unixFileCopy(File src, File dst){
            String content  = readFileToString(src);
            if (content == null) {return false;}
            
            Pattern p       = Pattern.compile("\r\n");
            Matcher m       = p.matcher(content);
            String newStr   = m.replaceAll("\n");
            return writeFileFromString( newStr, dst);
  } 
  

    //****************************************************************//
   //* Reads number of line in the text
   //* 
   //
   //****************************************************************//
  public static int countNumberOfLines(String content){
            if (content == null) {return 0;}

            Scanner scanner = new Scanner(content);
            int count       = 0;
            while(scanner.hasNextLine()){
                scanner.nextLine();
                count ++;
            }
            scanner.close();
            return count;
  }






    public static void main(String [] arg){
      /*
        File src                   = new File("josh 1.data"); 
       // File dst                   = new File("testing.txt");
        
        //win2unixFileCopy(src, dst);

        src                         =   new File("/Users/apple/BayesSys/Bayes.test.data/AsciiImages/asciiImageLarge" );
        long t1   = System.nanoTime();
        //getNumberOfLines(src.getPath());
        //   int i = getNumberOfLinesFast(src.getPath());
         //   int i = getNumberOfLines(src.getPath());
          long t2   = System.nanoTime();
          double time = (t2 - t1)*10e+9;
       */
      File file = new File("/Users/apple/NetBeansProjects/BayesServer/test/system/sbin/BayesSubmit_EnterAscii");
    
             writeFileFromString("test", file) ;
    }

    

 

}








