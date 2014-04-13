package msc.sde.chat.service.display.impl;// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import msc.sde.chat.client.ChatClient;

import java.io.IOException;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole extends AbstractConsole {
    //Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5500;

    //Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */

    //Constructors ****************************************************

    /**
     * Constructs an instance of the ClientConsole UI.
     *
     * @param loginId
     * @param host    The host to connect to.
     * @param port    The port to connect on.
     */
    public ClientConsole(String loginId, String host, int port) {
        try {
            console = new ChatClient(host, port, this, loginId);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
            System.exit(1);
        }
    }


    //Instance methods ************************************************

    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     */
    public static void main(String[] args) {
        String loginId = null;
        String host;
        int port;

        try {
            if (args == null || args.length == 0) {
                System.out.println("A Login Id need to be provided for the client connection");
                System.exit(0);
            }
            loginId = args[0];
            host = args[1];
            port = Integer.parseInt(args[2]);
        } catch (Exception e) {
            host = "localhost";
            port = DEFAULT_PORT;
        }
        ClientConsole chat = new ClientConsole(loginId, host, port);
        chat.console.init();
        chat.accept();  //Wait for console data
    }
}
//End of ConsoleChat class
