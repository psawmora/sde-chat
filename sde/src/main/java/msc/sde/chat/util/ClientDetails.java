package msc.sde.chat.util;

import ocsf.server.ConnectionToClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: prabath
 */
public class ClientDetails {

    private String ip;

    private String userId;

    private String password;

    private boolean isLoggedIn = false;

    private ConnectionToClient connectionToClient;

    private List<String> groupIdList;

    private List<String> budyIdList;

    private String forwardId;

    public ClientDetails(String ip,
                         String userId,
                         String password,
                         boolean loggedIn,
                         ConnectionToClient connectionToClient) {
        this.ip = ip;
        this.userId = userId;
        this.password = password;
        this.isLoggedIn = loggedIn;
        this.connectionToClient = connectionToClient;
        this.groupIdList = new ArrayList<>();
        this.budyIdList = new ArrayList<>();
    }

    public List<String> getBudyIdList() {
        return budyIdList;
    }

    public void addToBuddyList(String userId) {
        budyIdList.add(userId);
    }

    public void removeFromBuddyList(String userId) {
        budyIdList.remove(userId);
    }

    public String getForwardId() {
        return forwardId;
    }

    public void setForwardId(String forwardId) {
        this.forwardId = forwardId;
    }

    public List<String> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<String> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public ConnectionToClient getConnectionToClient() {
        return connectionToClient;
    }

    public void setConnectionToClient(ConnectionToClient connectionToClient) {
        this.connectionToClient = connectionToClient;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addToGroupIdList(String groupId) {
        groupIdList.add(groupId);
    }
}
