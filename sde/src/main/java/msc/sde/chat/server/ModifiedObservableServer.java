package msc.sde.chat.server;

import ocsf.server.ConnectionToClient;
import ocsf.server.ObservableServer;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/11/14
 * Time: 8:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModifiedObservableServer extends ObservableServer {

    public ModifiedObservableServer(int port) {
        super(port);
    }

    @Override
    protected synchronized void clientConnected(ConnectionToClient client) {
        setChanged();
        notifyObservers(new Message(CLIENT_CONNECTED, client));
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        setChanged();
        notifyObservers(new Message(CLIENT_DISCONNECTED, client));
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        setChanged();
        notifyObservers(new Message(CLIENT_EXCEPTION, client, exception));
        try
        {
            client.close();
        }
        catch (Exception e) {}
    }

    @Override
    protected synchronized void listeningException(Throwable exception) {
        setChanged();
        notifyObservers(new Message(LISTENING_EXCEPTION, exception));
        stopListening();
    }

    @Override
    protected synchronized void serverStopped() {
        setChanged();
        notifyObservers(new Message(SERVER_STOPPED));
    }

    @Override
    protected synchronized void serverClosed() {
        setChanged();
        notifyObservers(new Message(SERVER_CLOSED));
    }

    @Override
    protected synchronized void serverStarted() {
        setChanged();
        notifyObservers(new Message(SERVER_STARTED));
    }

    @Override
    protected synchronized void handleMessageFromClient(Object message, ConnectionToClient client) {
        setChanged();
        notifyObservers(new Message("handleMessage", (String)message, client));
    }

    public static class Message {

        private String functionName;

        private ConnectionToClient client;

        private Throwable exception;

        private String content;

        public Message(String functionName) {
            this.functionName = functionName;
        }

        public Message(String functionName, ConnectionToClient client) {
            this.functionName = functionName;
            this.client = client;
        }

        public Message(String functionName, Throwable exception) {
            this.functionName = functionName;
            this.exception = exception;
        }

        public Message(String functionName, String content, ConnectionToClient client) {
            this.functionName = functionName;
            this.content = content;
            this.client = client;
        }

        public Message(String functionName, ConnectionToClient client, Throwable exception) {
            this.functionName = functionName;
            this.client = client;
            this.exception = exception;
        }

        public String getFunctionName() {
            return functionName;
        }

        public ConnectionToClient getClient() {
            return client;
        }

        public Throwable getException() {
            return exception;
        }

        public String getContent() {
            return content;
        }
    }

}
