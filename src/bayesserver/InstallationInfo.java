    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bayesserver;
import utilities.IO;
import java.io.*;
import java.util.Scanner;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 *
 * @author apple
 */
public class InstallationInfo implements Constants{

    private String InstallationInterfaceVersion     = "";
    private String InstallerVersion                 = "";
    private String date                             = "";
    private boolean isLoaded                        =   false;
    private String  fortanCompiler                  =   NOCOMPILER;
    private String  cCompiler                       =   NOCOMPILER;
    private String  email                           =   "";
    private String  account                         =   "";
    private String  queue                           =   "None";
    private int     cpu                             =   1;
    private int     port                            =   8080;

    private boolean password                        =   true;
    private boolean subscribeUpdates                =   true;
    
    public  static final String SPLIT               =  "=";
    public  static final String INTERFACE_VERSION_KEY=  "INTERFACE VERSION";
    public  static final String INSTALLER_VERSION_KEY=  "INSTALLER VERSION";
    public  static final String DATE_KEY            =  "INSTALLATION DATE";
    public  static final String FORTRAN_COMPILER_KEY=  "FORTRAN COMPILER";
    public  static final String C_COMPILER_KEY      =  "C COMPILER";
    public  static final String EMAIL_KEY           =  "SERVER ADMINISTRATOR EMAIL";
    public  static final String CPU_KEY             =  "NUMBER OF CPUs";
    public  static final String PASSWORD_KEY        =  "PASSWORD";
    public  static final String ACCOUNT_KEY         =  "ACCOUNT";
    public  static final String PORT_KEY            =  "PORT";
    public  static final String REGISTER_EMAIL_KEY  =  "EMAIL SUBSCRIPTION SET";
    public  static final String QUEUE_KEY           =  "QUEUE";
    


    public InstallationInfo (){

    }


    public static InstallationInfo  loadFromFile(File file){
        InstallationInfo ii         =   new InstallationInfo();
        String content              =   IO.readFileToString(file);
        if(content  == null) {return ii;}

        Scanner scanner             =   new Scanner(content);
        while (scanner.hasNextLine()){
            String line             =   scanner.nextLine().trim();
            String value            =   getValue(line);
         
            if      (line.startsWith(INTERFACE_VERSION_KEY)){
                ii.setInstallationInterfaceVersion(value );
            }
            if      (line.startsWith(INSTALLER_VERSION_KEY)){
                ii.setInstallerVersion(value );
            }
            else if (line.startsWith(DATE_KEY)){
                ii.setDate(value );
            }
            else if (line.startsWith(ACCOUNT_KEY)){
                ii.setAccount(value );
            }
            else if (line.startsWith(FORTRAN_COMPILER_KEY)){
                ii.setFortanCompiler(value );
            }
            else if (line.startsWith(C_COMPILER_KEY)){
                ii.setcCompiler(value );
            }
            else if (line.startsWith(EMAIL_KEY)){
                ii.setEmail(value );
            }
            else if (line.startsWith(CPU_KEY)){
                try{
                    int cpu = Integer.parseInt(value);
                    ii.setCpu(cpu);
                } catch(Exception exp){/* do nothing */}

            }
            else if (line.startsWith(PORT_KEY)){
                try{
                    int p = Integer.parseInt(value);
                    ii.setPort(p);
                } catch(Exception exp){/* do nothing */}

            }
            else if (line.startsWith(PASSWORD_KEY)){
                try{
                    boolean isPswrd = Boolean.parseBoolean(value);
                    ii.setPassword(isPswrd);
                } catch(Exception exp){/* do nothing */}

            }
              else if (line.startsWith(REGISTER_EMAIL_KEY)){
                try{
                    boolean isSubscripe = Boolean.parseBoolean(value);
                    ii.setSubscribeForUpdates(isSubscripe);
                } catch(Exception exp){/* do nothing */}

            }
            else if (line.startsWith(QUEUE_KEY)){
                ii.setQueue(value );
            }

            ii.setIsLoaded(true);
        }


        return ii;
    }
    public void  writeToFile(File file){
        StringBuilder sb            =   new StringBuilder();

        sb.append(writeKeyValue(DATE_KEY            ,  getDateTime()));
        sb.append(writeKeyValue(INTERFACE_VERSION_KEY         ,  InstallationInterfaceVersion  ));
        sb.append(writeKeyValue(INSTALLER_VERSION_KEY         ,  InstallerVersion  ));
        sb.append(writeKeyValue(ACCOUNT_KEY         , getAccount()) );
        sb.append(writeKeyValue(PORT_KEY            , ""+getPort()) );
        sb.append(writeKeyValue(QUEUE_KEY           ,  getQueue()) );
        sb.append(writeKeyValue(FORTRAN_COMPILER_KEY, getFortanCompiler() ));
        sb.append(writeKeyValue(C_COMPILER_KEY      ,  getcCompiler()));
        sb.append(writeKeyValue(CPU_KEY             ,  ""+getCpu()));
        sb.append(writeKeyValue(PASSWORD_KEY        ,  ""+isPassword()));
        sb.append(writeKeyValue(REGISTER_EMAIL_KEY  ,  ""+ isSubscribeForUpdates()));



        if (getEmail() != null && getEmail().length() > 0){
            sb.append(writeKeyValue(EMAIL_KEY        , getEmail()));
        }

        IO.writeFileFromString(sb.toString(), file);

    }

    public String writeKeyValue(String key, String value){
        int     padlen              =   28;
        String  pad                 =   " ";
        StringBuilder sb            =   new StringBuilder();

        sb.append(pad(key ,padlen , pad ));
        sb.append(SPLIT);
        sb.append( "  "+ value );
        sb.append("\n" );
        return sb.toString();

    }



    public static String getValue(String line){
        String [] keyvalue  = line.split(SPLIT );
        String out          =    null;
        if (keyvalue.length == 2){out     =   keyvalue[1].trim();}

        return out;

    }
    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy/");
        return dateFormat.format(new Date()).toString();
    }

    public String       getInstallationInterfaceVersion() {
        return InstallationInterfaceVersion;
    }
    public void         setInstallationInterfaceVersion(String InstalaltionVersion) {
        this.InstallationInterfaceVersion = InstalaltionVersion;
    }

    public String       getFortanCompiler() {
        return fortanCompiler;
    }
    public void         setFortanCompiler(String fortanCompiler) {
        this.fortanCompiler = fortanCompiler;
    }

    public String       getcCompiler() {
        return cCompiler;
    }
    public void         setcCompiler(String cCompiler) {
        this.cCompiler = cCompiler;
    }


    public boolean      isLoaded() {
        return isLoaded;
    }
    public void         setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public String       getDate() {
        return date;
    }
    public void         setDate(String date) {
        this.date = date;
    }



    public static void main (String [] args){
        System.out.println( getDateTime() );
    }

    public String       getEmail() {
        return email;
    }
    public void         setEmail(String email) {
        this.email = email;
    }

    public int          getCpu() {
        return cpu;
    }
    public void         setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int          getPort() {
        return port;
    }
    public void         setPort(int port) {
        this.port = port;
    }

    public boolean      isPassword() {
        return password;
    }
    public void         setPassword(boolean password) {
        this.password = password;
    }

    public String       getAccount() {
        return account;
    }
    public void         setAccount(String account) {
        this.account = account;
    }

    public boolean      isFortanCompiler() {
        if (this.getFortanCompiler() == null)           {return false;}
        if (this.getFortanCompiler().length()< 1)       {return false;}
        if (this.getFortanCompiler().equals(NOCOMPILER)){return false;}

        return true;
    }
    public boolean      isCCompiler() {
        if (this.getcCompiler() == null)           {return false;}
        if (this.getcCompiler().length()< 1)       {return false;}
        if (this.getcCompiler().equals(NOCOMPILER)){return false;}

        return true;
    }

    public boolean      isSubscribeForUpdates() {
        return subscribeUpdates;
    }
    public void         setSubscribeForUpdates(boolean subscribeUpdates) {
        this.subscribeUpdates = subscribeUpdates;
    }

    public String       getQueue() {
        return queue;
    }
    public void         setQueue(String queue) {
        this.queue = queue;
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

    public String getInstallerVersion() {
        return InstallerVersion;
    }

    public void setInstallerVersion(String InstallerVersion) {
        this.InstallerVersion = InstallerVersion;
    }


    
    
}
