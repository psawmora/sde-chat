// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package msc.sde.chat.client;

import msc.sde.chat.service.ConsoleObserver;
import msc.sde.chat.service.display.ChatIF;
import ocsf.client.AbstractClient;

import java.io.IOException;

import static msc.sde.chat.util.Operations.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient implements ConsoleObserver {
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    private ChatIF clientUI;

    private String loginId;


    //Constructors ****************************************************


    public ChatClient(String host, int port, ChatIF clientUI, String loginId) throws IOException {
        this(host, port, clientUI);
        this.loginId = loginId;
    }

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */
    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
    }

    public void init() {
        /*try {
            openConnection();
        } catch (IOException e) {
            System.out.println("Exception occurred at initializing the client. " + e);
        }*/
    }

    //Instance methods ************************************************

    @Override
    protected void connectionClosed() {
        clientUI.display("Client Connection Closed");
    }


    @Override
    protected void connectionException(Exception e) {
        clientUI.display("Exception occurred in the connection. Closing the connection. " + e);
        quit();
    }

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        clientUI.display(msg.toString());
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMsgFromUi(String message) {
        try {
            if (message.startsWith("#")) {
                String[] cmnd = message.split(" ");
                if (cmnd.length > 0) {
                    handleClientCommands(cmnd[0].substring(1), cmnd);
                }
            } else if (isConnected()) {
                sendToServer(message);
            }
        } catch (IOException e) {
            clientUI.display
                    ("Could not send message to server.  Terminating client.");
            quit();
        }
    }

    private void handleClientCommands(String command, String... params) {
        switch (command) {
            case SIGN_UP:
                signUp(params);
                break;
            case QUIT:
                quit();
                break;
            case LOG_OFF:
                disconnect();
                break;
            case SET_HOST:
                setUpHost(params);
                break;
            case SET_PORT:
                setUpPort(params);
                break;
            case LOG_IN:
                login(params);
                break;
            case GET_HOST:
                clientUI.display("Host is : " + getHost());
                break;
            case GET_PORT:
                clientUI.display("Port is : " + getPort());
                break;
            default:
                clientUI.display("Wrong command");
                break;
        }
    }

    private void signUp(String[] params) {
        if (params != null && params.length >= 4) {
            try {
                openConnection();
                sendSignUpMessage(params);
            } catch (Exception e) {
                System.out.println("Error in signing up: [" + getPort() + "] : [" + getHost() + "] " + e);
            }
        } else {
            clientUI.display("Incorrect number of parameters for signing up");
        }
    }

    private void sendSignUpMessage(String[] params) throws IOException {
        StringBuffer request = new StringBuffer(params[0]).
                append(" ").
                append(params[1]).
                append(" ").
                append(params[2]).
                append(" ").
                append(params[3]);
        sendToServer(request.toString());
    }

    private void login(String[] params) {
        if (params != null && params.length >= 3) {
            try {
                boolean isConnected = isConnected();
                openConnection();
                sendLoginDetails(params);
                if (!isConnected) {
                    clientUI.display("Connected to the server port[" + getPort() + "]");
                }
            } catch (Exception e) {
                System.out.println("Error in logging to the server : [" + getPort() + "] : [" + getHost() + "] " + e);
            }
        } else {
            clientUI.display("Incorrect number of parameters for logging");
        }
    }

    private void sendLoginDetails(String[] params) throws IOException {
        StringBuffer request = new StringBuffer(params[0]).
                append(" ").
                append(params[1]).
                append(" ").
                append(params[2]);
        sendToServer(request.toString());
    }

    private void setUpPort(String[] args) {
        if (isConnected()) {
            clientUI.display("Cannot set the port. Client is connected to a server already. Please logoff first.");
            return;
        }
        if (args != null && args.length > 1) {
            setPort(Integer.parseInt(args[1]));
            clientUI.display("Set Up the port to [" + args[1] + "]");
        } else {
            clientUI.display("Wrong parameters for setting up the port.");
        }
    }

    private void setUpHost(String[] args) {
        if (isConnected()) {
            clientUI.display("Cannot set the host. Client is connected to a server already. Please logoff first.");
            return;
        }
        if (args != null && args.length > 1) {
            setHost(args[1]);
            clientUI.display("Set Up the host to [" + args[1] + "]");
        } else {
            clientUI.display("Wrong parameters for setting up the host.");
        }

    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        disconnect();
        clientUI.display("Stopping the client");
        System.exit(0);
    }

    private void disconnect() {
        try {
            closeConnection();
            clientUI.display("Logged off from the system");
        } catch (IOException e) {
        }
    }
}
//End of ChatClient class
