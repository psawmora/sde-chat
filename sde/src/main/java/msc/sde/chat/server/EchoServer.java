package msc.sde.chat.server;// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import msc.sde.chat.service.Console;
import msc.sde.chat.service.display.ChatIF;
import msc.sde.chat.service.send.SenderService;
import msc.sde.chat.service.send.impl.PrivateMsgSenderService;
import msc.sde.chat.service.group.GroupManager;
import msc.sde.chat.service.send.BroadcastSenderService;
import msc.sde.chat.util.*;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

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
public class EchoServer implements Observer, Console {

    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5500;

    private ChatIF serverUi;

    private Validator signUpValidator;

    private Validator loginValidator;

    private Validator privateMsgValidator;

    private Validator forwardValidator;

    private SenderService privateMsgSenderService;

    private Map<String, ClientDetails> clientDetailsContainer;

    private SenderService broadcastSenderService;

    private GroupManager groupManager;

    private ModifiedObservableServer server;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ChatIF serverUi) {
        server = new ModifiedObservableServer(port);
        server.addObserver(this);
        this.serverUi = serverUi;
        this.clientDetailsContainer = new HashMap<>();
        this.signUpValidator = new SignUpValidator();
        this.loginValidator = new LoginValidator();
        this.privateMsgValidator = new PrivateMsgValidator();
        this.privateMsgSenderService = new PrivateMsgSenderService();
        this.groupManager = new GroupManager(this);
        this.forwardValidator = new ForwardValidator();
        this.broadcastSenderService = new BroadcastSenderService(groupManager);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Updated oberver method..." + arg);
        if(arg instanceof ModifiedObservableServer.Message) {
            ModifiedObservableServer.Message message = (ModifiedObservableServer.Message) arg;
            switch (message.getFunctionName()) {
                case ModifiedObservableServer.CLIENT_CONNECTED:
                    clientConnected(message.getClient());
                    break;
                case ModifiedObservableServer.CLIENT_DISCONNECTED:
                    clientDisconnected(message.getClient());
                    break;
                case ModifiedObservableServer.CLIENT_EXCEPTION:
                    clientException(message.getClient(), message.getException());
                    break;
                case ModifiedObservableServer.LISTENING_EXCEPTION:
                    break;
                case ModifiedObservableServer.SERVER_CLOSED:
                    break;
                case ModifiedObservableServer.SERVER_STARTED:
                    break;
                case ModifiedObservableServer.SERVER_STOPPED:
                    break;
                default:
                    handleMessageFromClient(message.getContent(), message.getClient());
                    break;
            }
        } else {
            System.out.println("Unknown argument type");
        }
    }

    @Override
    public void init() {
        try {
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void handleMsgFromUi(String message) {
        if (message.startsWith("#")) {
            extractCommand(message);
        } else {
            String serverMessage = "SERVER MSG> " + message;
            serverUi.display(serverMessage);
            server.sendToAllClients(serverMessage);
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
                server.stopListening();
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
                serverUi.display("Server Port : " + server.getPort());
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
        if (server.isListening()) {
            serverUi.display("Server is already listening on port[" + server.getPort() + "]. Cannot start again");
            return;
        }
        try {
            server.listen();
        } catch (IOException e) {
            System.out.println("Error in start listning on port[" + server.getPort() + " " + e);
        }
    }


    private void setUpPort(String[] args) {
        if (server.isListening()) {
            serverUi.display("Cannot set the port. Server is listening to clients already. Please close first.");
            return;
        }
        if (args != null && args.length > 1) {
            server.setPort(Integer.parseInt(args[1]));
        } else {
            serverUi.display("Wrong parameters for setting up the port.");
        }
    }

    private void closeAllClients() {
        try {
            Thread[] clientConnections = server.getClientConnections();
            for (Thread client : clientConnections) {
                ((ConnectionToClient) client).close();
            }
            server.close();
        } catch (Exception e) {
            System.out.println("Error in stopping the server. " + e);
        }
    }

    private void quit() {
        try {
            server.close();
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
                    //
                }
            } else {
                System.out.println("User Message: " + msg + " from " + loginId);
                StringBuffer newMsg = new StringBuffer(loginId).append(">").append(msg);
                try {
                    broadcastSenderService.send(this, loginId, newMsg.toString());
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
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
                    case PRIVATE_MSG:
                        sendPrivateMsg(client, params);
                        break;
                    case CREATE_GROUP:
                        handleCreateGroup(client, params);
                        break;
                    case ASSIGN_TO_GROUP:
                        assignToGroup(client, params);
                        break;
                    case RESIGN_FROM_GROUP:
                        resignFromGroup(client, params);
                        break;
                    case FORWARD:
                        forward(client, params);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception occurred while processing client message " + e);
        }
    }

    private void forward(ConnectionToClient client, String[] params) throws IOException {
        ValidateResult validate = forwardValidator.validate(this, client, params);
        client.sendToClient(validate.getReturnMsg());
    }

    private void sendPrivateMsg(ConnectionToClient client, String[] params) throws IOException {
        ValidateResult result = privateMsgValidator.validate(this, client, params);
        if (result.isValid()) {
            String userId = params[1];
            String msg = params[2];
            privateMsgSenderService.send(this, userId, client.getInfo("loginId") + ">" + msg);
            client.sendToClient(String.format("Private message sent to %s", userId));
        } else {
            client.sendToClient(result.getReturnMsg());
        }
    }

    private void resignFromGroup(ConnectionToClient client, String[] params) throws IOException {
        ValidateResult validateResult = groupManager.resignFromGroup(client, params);
        client.sendToClient(validateResult.getReturnMsg());
    }

    private void assignToGroup(ConnectionToClient client, String[] params) throws IOException {
        ValidateResult validateResult = groupManager.addClientToGroup(client, params);
        client.sendToClient(validateResult.getReturnMsg());
    }

    private void handleCreateGroup(ConnectionToClient client, String[] params) throws IOException {
        ValidateResult validateResult = groupManager.handleCreateGroup(client, params);
        client.sendToClient(validateResult.getReturnMsg());
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
                ("Server listening for connections on port " + server.getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println
                ("Server has stopped listening for connections.");
    }

    protected void clientConnected(ConnectionToClient client) {
        System.out.println(
                "Client [" + client.getId() + "] from [" + client.getInetAddress().getHostAddress() +
                        "] connected to the server");
    }

    protected synchronized void clientDisconnected(ConnectionToClient client) {
        System.out.println(
                "Client [" + client.getId() + "] from [" + client.getInetAddress().getHostAddress() +
                        "] disconnected from the server");

    }

    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        String id = (String) client.getInfo("loginId");
        clientDetailsContainer.get(id).setLoggedIn(false);
        System.out.println(
                "Client [" + id + "] disconnected from the server due to an exception");
    }

    public ClientDetails getClientDetails(String userId) {
        return clientDetailsContainer.get(userId);
    }

    public void putClientDetails(String userId, ClientDetails details) {
        clientDetailsContainer.put(userId, details);
    }

    public Map<String, ClientDetails> getClientDetailsContainer() {
        return clientDetailsContainer;
    }

    public boolean isContainsClientDetails(String userId) {
        return userId == null ? false : clientDetailsContainer.containsKey(userId);
    }

    public ModifiedObservableServer getServer() {
        return server;
    }
}
//End of EchoServer class
