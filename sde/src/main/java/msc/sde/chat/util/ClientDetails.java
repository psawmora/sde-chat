package msc.sde.chat.util;

/**
 * @author: prabath
 */
public class ClientDetails {

    private String ip;

    private String userId;

    private String password;

    public ClientDetails() {
    }

    public ClientDetails(String ip, String userId, String password) {

        this.ip = ip;
        this.userId = userId;
        this.password = password;
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
