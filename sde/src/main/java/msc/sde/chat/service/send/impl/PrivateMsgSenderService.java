package msc.sde.chat.service.send.impl;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.service.send.SenderService;
import msc.sde.chat.util.ClientDetails;

import java.io.IOException;

public class PrivateMsgSenderService implements SenderService {

    @Override
    public void send(EchoServer server, String msg) {
         server.sendToAllClients(msg);
    }

    @Override
    public void send(EchoServer server, String msg, String id) throws IOException {
        ClientDetails clientDetails = server.getClientDetails(id);
        clientDetails.getConnectionToClient().sendToClient(msg);
    }
}
