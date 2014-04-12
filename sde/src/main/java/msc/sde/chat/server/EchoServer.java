package msc.sde.chat.server;// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import msc.sde.chat.service.ConsoleObserver;
import msc.sde.chat.service.display.ChatIF;
import msc.sde.chat.util.*;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static msc.sde.chat.util.Operations.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer implements ConsoleObserver {

    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5500;

    private ChatIF serverUi;

    private Validator signUpValidator;

    private Validator loginValidator;

    private Map<String, ClientDetails> clientDetailsContainer;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ChatIF serverUi) {
        super(port);
        this.serverUi = serverUi;
        this.clientDetailsContainer = new HashMap<>();
        this.signUpValidator = new SignUpValidator();
        this.loginValidator = new LoginValidator();
    }

    @Override
    public void init() {

    }

    public void handleMsgFromUi(String message) {
        if (message.startsWith("#")) {
            extractCommand(message);
        } else {
            String serverMessage = "SERVER MSG> " + message;
            serverUi.display(serverMessage);
            sendToAllClients(serverMessage);
        }
    }

    private void extractCommand(String message) {
        String[] cmnd = message.split(" ");
        if (cmnd.length > 0) {
            handleServerCommands(cmnd[0].substring(1), cmnd);
        }
    }

    private void handleServerCommands(String command, String... args) {
        switch (command) {
            case QUIT:
                quit();
                break;
            case STOP:
                stopListening();
                break;
            case CLOSE:
                closeAllClients();
                break;
            case SET_PORT:
                setUpPort(args);
                break;
            case START:
                startIfNot();
                break;
            case GET_PORT:
                serverUi.display("Server Port : " + getPort());
                break;
            default:
                serverUi.display("Wrong command");
                break;
        }

    }

    private void loginClient(ConnectionToClient client, String[] params) throws IOException {
        ValidateResult result = loginValidator.validate(this, client, params);
        client.sendToClient(result.getReturnMsg());

    }

    private void startIfNot() {
        if (isListening()) {
            serverUi.display("Server is already listening on port[" + getPort() + "]. Cannot start again");
            return;
        }
        try {
            listen();
        } catch (IOException e) {
            System.out.println("Error in start listning on port[" + getPort() + " " + e);
        }
    }


    private void setUpPort(String[] args) {
        if (isListening()) {
            serverUi.display("Cannot set the port. Server is listening to clients already. Please close first.");
            return;
        }
        if (args != null && args.length > 1) {
            setPort(Integer.parseInt(args[1]));
        } else {
            serverUi.display("Wrong parameters for setting up the port.");
        }
    }

    private void closeAllClients() {
        try {
            Thread[] clientConnections = getClientConnections();
            for (Thread client : clientConnections) {
                ((ConnectionToClient) client).close();
            }
            close();
        } catch (Exception e) {
            System.out.println("Error in stopping the server. " + e);
        }
    }

    private void quit() {
        try {
            close();
            serverUi.close();
        } catch (Exception e) {
            System.out.println("Error in stopping the server. " + e);
        }
    }

    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof String && ((String) msg).startsWith("#")) {
            System.out.println("User Command: " + msg + " from " + client);
            handleClientCommand(client, (String) msg);
        } else {
            String loginId = (String) client.getInfo("loginId");
            if (loginId == null) {
                try {
                    client.sendToClient("Error. No login id found for the client.");
                } catch (IOException e) {
                    System.out.println(
                            "Exception in sending messages to the client[ " + client.getInetAddress().getHostAddress() + "]");
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {

                    }
                }
            } else {
                System.out.println("User Message: " + msg + " from " + loginId);
                StringBuffer newMsg = new StringBuffer(loginId).append(">").append(msg);
                sendToAllClients(newMsg.toString());
            }

        }
    }

    private void handleClientCommand(ConnectionToClient client, String msg) {
        String[] params = msg.split(" ");
        String command;
        try {
            if (params.length > 0 && (command = params[0].substring(1)) != null) {
                switch (command) {
                    case SIGN_UP:
                        signUpClient(client, params);
                        break;
                    case LOG_IN:
                        loginClient(client, params);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception occurred while processing client message " + e);
        }
    }

    private void signUpClient(ConnectionToClient client, String[] params) throws IOException {
        SignUpValidator.SignUpValidatorResult result =
                (SignUpValidator.SignUpValidatorResult) signUpValidator.validate(this, client, params);
        if (result.isValid()) {
            putClientDetails(result.getClientDetails().getUserId(), result.getClientDetails());
        }
        client.sendToClient(result.getReturnMsg());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println
                ("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println
                ("Server has stopped listening for connections.");
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println(
                "Client [" + client.getId() + "] from [" + client.getInetAddress().getHostAddress() +
                        "] connected to the server");
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        System.out.println(
                "Client [" + client.getId() + "] from [" + client.getInetAddress().getHostAddress() +
                        "] disconnected from the server");

    }

    public ClientDetails getClientDetails(String userId) {
        return clientDetailsContainer.get(userId);
    }

    public void putClientDetails(String userId, ClientDetails details) {
        clientDetailsContainer.put(userId, details);
    }

    public boolean isContainsClientDetails(String userId) {
        return clientDetailsContainer.containsKey(userId);
    }

}
//End of EchoServer class
