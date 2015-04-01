/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;
import java.util.Scanner;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.*;
/**
 *
 * @author apple
 */
public class DisplayText {
    public static void popupMessage(Object message){


         final String text  = message.toString();
         boolean isEDT      = SwingUtilities.isEventDispatchThread();

         if (isEDT){popupMessageOnEDT (text);}

         else {
            SwingUtilities.invokeLater(new Runnable()
		{
			public void run(){popupMessageOnEDT (text);}
		});
         }
    }
    public static void popupMessageOnEDT(Object message){


         String text  = message.toString();
                JOptionPane.showMessageDialog(
                                new  javax.swing.JFrame(),
                                text,
                                "Message",
                                JOptionPane.WARNING_MESSAGE);

    }



    public static void popupWarningMessage(Object message){


         final String text  = message.toString();
         boolean isEDT      = SwingUtilities.isEventDispatchThread();

         if (isEDT){popupMessageOnEDT (text);}

         else {
            SwingUtilities.invokeLater(new Runnable()
		{
			public void run(){popupMessageOnEDT (text);}
		});
         }
    }
    private static void popupWarningMessageOnEDT(Object message){


         String text  = message.toString();
                JOptionPane.showMessageDialog(
                                new  javax.swing.JFrame(),
                                text,
                                "Message",
                                JOptionPane.WARNING_MESSAGE);

    }


    public static void  popupErrorMessage(Object message){


         final String text  = message.toString();
         boolean isEDT      = SwingUtilities.isEventDispatchThread();

         if (isEDT){popupMessageOnEDT (text);}

         else {
            SwingUtilities.invokeLater(new Runnable()
		{
			public void run(){popupMessageOnEDT (text);}
		});
         }
    }
    private static void  popupErroressageOnEDT(Object message){


         String text  = message.toString();
                JOptionPane.showMessageDialog(
                                new  javax.swing.JFrame(),
                                text,
                                "Message",
                                JOptionPane.ERROR_MESSAGE);

    }

    public static boolean popupDialog(final String text){

         boolean shoudProceed       = true;
         boolean isEDT              = SwingUtilities.isEventDispatchThread();
        


          if (isEDT){ shoudProceed  =  popupDialogFromEDT (text);}
          else {
            PopupDialogThread thread =  new PopupDialogThread(text);
           
             SwingUtilities.invokeLater(thread);
             while (thread.isAlive()){
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException ex) {
                   ex.printStackTrace();
                   return  false;

                }
             }
             shoudProceed           = thread.proceed;
		
         }




         return shoudProceed;
    }
    private static boolean popupDialogFromEDT( String text){

         boolean shoudProceed       = true;

                 int n = JOptionPane.showConfirmDialog(
                                new  javax.swing.JFrame(),
                                text, "Message",JOptionPane.YES_NO_OPTION);
                 if (n  ==  JOptionPane.NO_OPTION ){shoudProceed = false;}

         return shoudProceed;
    }



     static class PopupDialogThread  extends Thread{
         boolean proceed =  true;
         String text     =  null;

         PopupDialogThread (String atext){
            text  = atext;
         }
         public void run(){

				proceed = popupDialogFromEDT(text);
	 }
	}
}

