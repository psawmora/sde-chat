package msc.sde.chat.service.display.impl;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.server.ModifiedConnectionToClientFactory;

import static msc.sde.chat.server.EchoServer.DEFAULT_PORT;

public class ServerConsole extends AbstractConsole {

    public static void main(String[] args) {
        System.out.println("Starting the console for server");
        int port; //Port to listen on

        try {
            port = Integer.parseInt(args[0]); //Get port from command line
        } catch (Throwable t) {
            port = DEFAULT_PORT; //Set port to 5555
        }
        ServerConsole serverConsole = new ServerConsole();
        EchoServer server = new EchoServer(port, serverConsole);
        server.setConnectionFactory(new ModifiedConnectionToClientFactory());
        serverConsole.setObserver(server);
        try {
            server.listen(); //Start listening for connections
            serverConsole.accept();
        } catch (Exception ex) {
            System.out.println("Exception occurred. Server console stopped. " + ex);
        }

    }

    public void setObserver(EchoServer observer) {
        this.observer = observer;
    }
}
