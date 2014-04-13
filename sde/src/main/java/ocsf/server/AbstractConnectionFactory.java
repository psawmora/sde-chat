package ocsf.server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/11/14
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AbstractConnectionFactory {

    ConnectionToClient createConnection(ThreadGroup group, Socket clientSocket, AbstractServer server) throws IOException;

}
