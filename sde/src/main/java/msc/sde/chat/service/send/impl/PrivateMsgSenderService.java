package msc.sde.chat.service.send.impl;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.service.send.SenderService;
import msc.sde.chat.util.ClientDetails;

import java.io.IOException;
import java.util.List;

public class PrivateMsgSenderService implements SenderService {

    @Override
    public void send(EchoServer server, String id, String msg) throws IOException {
        ClientDetails clientDetails = server.getClientDetails(id);
        if (clientDetails.getForwardId() != null && !clientDetails.getForwardId().isEmpty()) {
            ClientDetails forwardClient = server.getClientDetails(clientDetails.getForwardId());
            if (forwardClient != null && forwardClient.isLoggedIn()) {
                forwardClient.getConnectionToClient().sendToClient(msg);
                return;
            }
        }
        clientDetails.getConnectionToClient().sendToClient(msg);
    }

    @Override
    public void send(EchoServer server, String msg, List<String> userIdList) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
