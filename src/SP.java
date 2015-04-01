/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
/**
 *
 * @author apple
 */
public class SP {
       public static void   getSystemProperties(){
        Properties p = System.getProperties ();
        java.util.Enumeration en = p.propertyNames();

        for (; en .hasMoreElements(); ) {


            String propName = (String)en.nextElement();
            String propValue = (String)p.getProperty(propName);
            System.out.println(propName + " =  "+ propValue);
        }
    }
       public static void main( String argsp[]){
             getSystemProperties();
       }
}
