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
            if (clientDetails != null && password.equals(clientDetails.getPassword())) {
                client.setInfo("isLoggedIn", true);
                client.setInfo("loginId", userId);
                clientDetails.setLoggedIn(true);
                return new LoginValidatorResult(true, "Login success");
            } else {
                return new LoginValidatorResult(false, "Login failed");
            }
        } else {
            return new LoginValidatorResult(false, "Incorrect parameter size");
        }
    }

    public static class LoginValidatorResult extends ValidateResult {

        public LoginValidatorResult(boolean valid, String returnMsg) {
            super(valid, returnMsg);
        }
    }

}



