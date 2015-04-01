/*
 * BayesServerApp.java
 */

package bayesserver;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class BayesServerApp extends SingleFrameApplication {
    public final static String VESRION  = "1.10";
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
       BayesServerView application = new BayesServerView(this);
      
        show( application);
        application.getFrame().setSize(950, 660);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of BayesServerApp
     */
    public static BayesServerApp getApplication() {
        return Application.getInstance(BayesServerApp.class);
    }



    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        // turn on anti-aliasing for smooth fonts.
        System.setProperty("java.net.preferIPv4Stack" , "true");
        System.setProperty( "swing.aatext", "true" );
        String homedir      =   System.getProperty("user.home");
        String curdir       =   System.getProperty("user.dir");
       
        if (homedir.equalsIgnoreCase( curdir ) == false){
            String text     =  String.format("The installation procedure must be run from the home directory\n" +
                                             "of the account that is to run the Bayesian Analsysis Software.\n" +
                                             "If you are logged into the correct account, then please\n" +
                                             "change to %s and rerun the installation procedure.", homedir );

            javax.swing.JOptionPane.showMessageDialog(
                                null,
                                text,
                                "Message",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
           System.exit(0);
        }


        launch(BayesServerApp.class, args);
    }
}
