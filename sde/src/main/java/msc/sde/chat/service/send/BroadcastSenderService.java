package msc.sde.chat.service.send;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.service.group.GroupManager;
import msc.sde.chat.util.ClientDetails;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: prabath
 */
public class BroadcastSenderService implements SenderService {

    private GroupManager groupManager;

    public BroadcastSenderService(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public void send(EchoServer server, String userId, String msg) throws IOException {
        ClientDetails clientDetails = server.getClientDetails(userId);
        if (clientDetails != null && !clientDetails.getGroupIdList().isEmpty()) {
            sendToGroups(server, clientDetails.getGroupIdList(), userId, msg);
        } else {
            broadcastToAll(server, msg);
        }

    }

    private void broadcastToAll(EchoServer server, String msg) throws IOException {
        Map<String, ClientDetails> clientDetailsMap = server.getClientDetailsContainer();
        for (ClientDetails clientDetails : clientDetailsMap.values()) {
            if (clientDetails.isLoggedIn()) {
                checkAndExecuteForwarding(server, clientDetails, msg);
            }
        }
    }

    private void checkAndExecuteForwarding(EchoServer server, ClientDetails clientDetails, String msg) throws IOException {
        if (clientDetails.getForwardId() != null && !clientDetails.getForwardId().isEmpty()) {
            ClientDetails forwardClient = server.getClientDetails(clientDetails.getForwardId());
            if (forwardClient != null && forwardClient.isLoggedIn()) {
                forwardClient.getConnectionToClient().sendToClient(msg);
                return;
            }
        }
        clientDetails.getConnectionToClient().sendToClient(msg);
    }

    private void sendToGroups(EchoServer server, List<String> groupIdList, String userId, String msg) throws IOException {
        for (String grpId : groupIdList) {
            GroupManager.Group group = groupManager.getGroupForId(grpId);
            if (group != null) {
                group.sendGroupMessage(userId, msg);
            }
        }
    }

    @Override
    public void send(EchoServer server, String msg, List<String> userIdList) throws IOException {

    }
}
