/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 * @author apple
 */
public class IP {
    public static void getIP(){

           try {
            InetAddress thisIp        = InetAddress.getLocalHost();
            String serverHostName            = thisIp.getHostName() ;

               System.out.println( "Host Name           = " + thisIp.getHostName());
               System.out.println( "Canonical Host Name = " + thisIp.getCanonicalHostName());
               System.out.println( "String represent.   = " + thisIp.toString());
               System.out.println( "IsLoopbackAddress   = " + thisIp.isLoopbackAddress());
               System.out.println( "IsAnyLocalAddress   = " + thisIp.isAnyLocalAddress());
        

            InetAddress[] all = InetAddress.getAllByName(thisIp.getHostName());
            for (int i=0; i<all.length; i++) {
                 System.out.println("  address = " + all[i]);
            }

           }catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
    }
      public static String getCurrentEnvironmentNetworkIp() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            System.err.println("Somehow we have a socket error...");
        }
        try{
          while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress addr = address.nextElement();
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                        && !(addr.getHostAddress().indexOf(":") > -1)) {
                    System.out.println("CurrentEnvironmen Host = "+  addr.getCanonicalHostName());
                    System.out.println("CurrentEnvironmen Host = "+  addr.getHostAddress());
                    return addr.getHostAddress();
                }
            }
        }

        }
        catch (Exception e) {
           e.printStackTrace();
        }

  
        try {
            
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    public static void main (String args []){

        getIP();
        getCurrentEnvironmentNetworkIp();

    }
}
