package msc.sde.chat.util.validator;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.util.ClientDetails;
import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public class SignUpValidator implements Validator {

    @Override
    public SignUpValidatorResult validate(EchoServer echoServer, ConnectionToClient client, String... params) {

        Object isLoggedIn = client.getInfo("isLoggedIn");
        if (isLoggedIn != null && (Boolean) isLoggedIn) {
            return new SignUpValidatorResult(false, "You have already Signed Up");
        }
        if (params != null && params.length >= 4) {
            String userId = params[1];
            String password = params[2];
            String retypedPassword = params[3];
            ClientDetails clientDetails = echoServer.getClientDetails(userId);
            if (clientDetails == null) {
                if (password.equals(retypedPassword)) {
                    return new SignUpValidatorResult(true, "Sign up succeeded", createClientDetails(client, userId, password));
                } else {
                    return new SignUpValidatorResult(false, "Retyped password not matched");
                }
            } else {
                return new SignUpValidatorResult(false, "User id has already taken");
            }
        } else {
            return new SignUpValidatorResult(false, "Incorrect parameter size");
        }
    }

    private ClientDetails createClientDetails(ConnectionToClient connectionToClient,
                                              String userId,
                                              String password) {
        return new ClientDetails(connectionToClient.getInetAddress().getHostAddress(), userId, password,false,connectionToClient);
    }

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String[] params, boolean removeForwarding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class SignUpValidatorResult extends ValidateResult {

        private ClientDetails clientDetails;

        public SignUpValidatorResult(boolean valid, String returnMsg, ClientDetails clientDetails) {
            super(valid, returnMsg);
            this.clientDetails = clientDetails;
        }

        public SignUpValidatorResult(boolean valid, String returnMsg) {
            super(valid, returnMsg);
        }

        public ClientDetails getClientDetails() {
            return clientDetails;
        }
    }
}
