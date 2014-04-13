package msc.sde.chat.util;

import msc.sde.chat.server.EchoServer;
import ocsf.server.ConnectionToClient;

public class PrivateMsgValidator implements Validator {

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String... params) {
        if (params != null && params.length >= 3) {
            String userId = params[1];
            String msg = params[2];
            ClientDetails clientDetails = server.getClientDetails(userId);
            if(clientDetails != null) {
                if(clientDetails.isLoggedIn()) {
                    return new PrivateMsgValidatorResult(true, "Success");
                } else {
                    return new PrivateMsgValidatorResult(false, "The specified user is not logged in");
                }
            } else {
                return new PrivateMsgValidatorResult(false, "Invalid destination user ID");
            }
        } else {
            return new PrivateMsgValidatorResult(false, "Incorrect parameter size");
        }
    }

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String[] params, boolean removeForwarding) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class PrivateMsgValidatorResult extends ValidateResult {
        public PrivateMsgValidatorResult(boolean valid, String returnMsg) {
            super(valid, returnMsg);
        }
    }

}
