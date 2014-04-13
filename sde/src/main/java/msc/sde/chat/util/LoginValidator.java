package msc.sde.chat.util;

import msc.sde.chat.server.EchoServer;
import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public class LoginValidator implements Validator {

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String... params) {
        if (params != null && params.length >= 3) {
            String userId = params[1];
            String password = params[2];
            ClientDetails clientDetails = server.getClientDetails(userId);
            if (clientDetails != null && !clientDetails.isLoggedIn() && password.equals(clientDetails.getPassword())) {
                client.setInfo("isLoggedIn", true);
                client.setInfo("loginId", userId);
                clientDetails.setLoggedIn(true);
                clientDetails.setConnectionToClient(client);
                return new LoginValidatorResult(true, "Login success", userId);
            } else {
                return new LoginValidatorResult(false, "Login failed", userId);
            }
        } else {
            return new LoginValidatorResult(false, "Incorrect parameter size", null);
        }
    }

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String[] params, boolean removeForwarding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class LoginValidatorResult extends ValidateResult {

        private String userId;

        public LoginValidatorResult(boolean valid, String returnMsg, String userId) {
            super(valid, returnMsg);
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

}



