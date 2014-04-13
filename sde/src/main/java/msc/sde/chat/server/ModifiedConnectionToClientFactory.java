package msc.sde.chat.server;

import ocsf.server.AbstractConnectionFactory;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/11/14
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModifiedConnectionToClientFactory implements AbstractConnectionFactory {

    @Override
    public ConnectionToClient createConnection(ThreadGroup group, Socket clientSocket, AbstractServer server) throws IOException {
        return new ModifiedConnectionToClient(group, clientSocket, server);
    }
}
