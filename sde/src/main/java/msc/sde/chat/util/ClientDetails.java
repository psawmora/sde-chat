package msc.sde.chat.util;

import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public class ClientDetails {

    private String ip;

    private String userId;

    private String password;

    private boolean isLoggedIn = false;

    private ConnectionToClient connectionToClient;

    public ClientDetails() {
    }

    public ClientDetails(String ip,
                         String userId,
                         String password,
                         boolean loggedIn,
                         ConnectionToClient connectionToClient) {
        this.ip = ip;
        this.userId = userId;
        this.password = password;
        isLoggedIn = loggedIn;
        this.connectionToClient = connectionToClient;
    }

    public ClientDetails(String ip, String userId, String password) {

        this.ip = ip;
        this.userId = userId;
        this.password = password;
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
}
