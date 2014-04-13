package msc.sde.chat.service.group;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.service.send.SenderService;
import msc.sde.chat.util.ClientDetails;
import msc.sde.chat.util.ValidateResult;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: prabath
 */
public class GroupManager {

    private EchoServer server;

    private Map<String, Group> groupContainer;

    private SenderService groupSenderService;

    public GroupManager(EchoServer server) {
        this.groupContainer = new HashMap<>();
        this.server = server;
        this.groupSenderService = new GroupSenderService();
    }

    public ValidateResult handleCreateGroup(ConnectionToClient client, String[] params) {
        boolean isLoggedIn = client.getInfo("isLoggedIn") == null ? false : (Boolean) client.getInfo("isLoggedIn");
        if (isLoggedIn && params != null && params.length >= 2) {
            String groupId = params[1];
            if (groupContainer.containsKey(groupId)) {
                return new GroupManagerValidateResult(false, "Group already exists for the given id");
            }
            String userId = (String) client.getInfo("loginId");
            if (server.isContainsClientDetails(userId)) {
                server.getClientDetails(userId).addToGroupIdList(groupId);
                Group group = new Group(userId, groupSenderService, server);
                groupContainer.put(groupId, group);
                return new GroupManagerValidateResult(true, "Group created successfully");
            }

        }
        return new GroupManagerValidateResult(false, "Group creation failed");
    }

    public ValidateResult addClientToGroup(ConnectionToClient client, String[] params) throws IOException {
        boolean isLoggedIn = client.getInfo("isLoggedIn") == null ? false : (Boolean) client.getInfo("isLoggedIn");
        String userId = (String) client.getInfo("loginId");
        if (isLoggedIn && params != null && params.length >= 3) {
            String groupId = params[1];
            String secondClient = params[2];
            if (!groupContainer.containsKey(groupId)) {
                return new GroupManagerValidateResult(false, "No group can be found for the group id");
            }
            if (server.isContainsClientDetails(secondClient)
                    && groupContainer.containsKey(groupId)
                    && groupContainer.get(groupId).getOwnerId().equals(userId)) {
                ClientDetails secondaryClientDetails = server.getClientDetails(secondClient);
                secondaryClientDetails.addToGroupIdList(groupId);
                secondaryClientDetails.getConnectionToClient().sendToClient("You have been added to the group : " + groupId);
                groupContainer.get(groupId).addToGroup(secondClient);
                return new GroupManagerValidateResult(true, "Added to group successfully");
            }

        }
        return new GroupManagerValidateResult(false, "Adding to group failed");
    }

    public ValidateResult resignFromGroup(ConnectionToClient client, String[] params) {
        boolean isLoggedIn = client.getInfo("isLoggedIn") == null ? false : (Boolean) client.getInfo("isLoggedIn");
        String userId = (String) client.getInfo("loginId");
        if (isLoggedIn && params != null && params.length >= 2) {
            String groupId = params[1];
            ClientDetails clientDetails = server.getClientDetails(userId);
            if (clientDetails != null && clientDetails.getGroupIdList().contains(groupId)) {
                boolean isSuccess = groupContainer.get(groupId).removeMember(userId);
                clientDetails.getGroupIdList().remove(groupId);
                return new GroupManagerValidateResult(isSuccess, "Resigning from group " + groupId + " is " + isSuccess);
            }
        }
        return new GroupManagerValidateResult(false, "Resigning from the group is a failure");
    }

    public ValidateResult deleteGroup(ConnectionToClient client, String[] params) {
        boolean isLoggedIn = client.getInfo("isLoggedIn") == null ? false : (Boolean) client.getInfo("isLoggedIn");
        String userId = (String) client.getInfo("loginId");
        if (isLoggedIn && params != null && params.length >= 2) {
            String grpId = params[1];
            Group group = groupContainer.get(grpId);
            if (group != null && group.getOwnerId().equals(userId)) {
                boolean isSuccess = removeUsersFromGroup(group, server, grpId);
                return new GroupManagerValidateResult(isSuccess, "Deleting group is " + isSuccess);
            }
        }
        return new GroupManagerValidateResult(false, "Deleting group is not  success");
    }

    private boolean removeUsersFromGroup(Group group, EchoServer server, String grpId) {
        for (String memeber : group.memeberList) {
            server.getClientDetails(memeber).getGroupIdList().remove(grpId);
        }
        group.memeberList.clear();
        return true;
    }

    public Group getGroupForId(String groupId) {
        return groupContainer.get(groupId);
    }

    public static class GroupManagerValidateResult extends ValidateResult {

        public GroupManagerValidateResult(boolean valid, String returnMsg) {
            super(valid, returnMsg);
        }
    }

    public static class GroupSenderService implements SenderService {

        @Override
        public void send(EchoServer server, String userId, String msg) throws IOException {

        }

        @Override
        public void send(EchoServer server, String msg, List<String> userIdList) throws IOException {
            for (String userId : userIdList) {
                if (server.isContainsClientDetails(userId)) {
                    ClientDetails clientDetails = server.getClientDetails(userId);
                    if (clientDetails.isLoggedIn()) {
                        checkAndExecuteForwarding(server, clientDetails, msg);
                    }
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
    }

    public static class Group {

        private EchoServer server;

        private String ownerId;

        private List<String> memeberList;

        private SenderService groupSenderService;

        public Group(String ownerId, SenderService groupSenderService, EchoServer server) {
            this.ownerId = ownerId;
            this.groupSenderService = groupSenderService;
            this.memeberList = new ArrayList<>();
            this.server = server;
            memeberList.add(ownerId);
        }

        public void addToGroup(String userId) {
            memeberList.add(userId);
        }

        public void sendGroupMessage(String userId, String msg) throws IOException {
            groupSenderService.send(server, msg, memeberList);
        }

        public String getOwnerId() {
            return ownerId;
        }

        public boolean removeMember(String userId) {
            return memeberList.remove(userId);
        }
    }
}
