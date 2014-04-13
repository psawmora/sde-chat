package msc.sde.chat.server;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/11/14
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModifiedConnectionToClient extends ConnectionToClient {


    public ModifiedConnectionToClient(ThreadGroup group, Socket clientSocket, AbstractServer server) throws IOException {
        super(group, clientSocket, server);
    }

}
