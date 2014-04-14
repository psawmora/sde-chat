package msc.sde.chat.util.validator;

import msc.sde.chat.server.EchoServer;
import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public class ForwardValidator implements Validator {

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String... params) {
        if (checkIfUserEligible(client, server, params)) {
            String userId = (String) client.getInfo("loginId");
            String forwardClientId = params[1];
            if (server.isContainsClientDetails(forwardClientId) && server.getClientDetails(forwardClientId).isLoggedIn()) {
                server.getClientDetails(userId).setForwardId(forwardClientId);
                return new ForwardValidatorResult(true, "Set the forward client node successfully");
            }
        }
        return new ForwardValidatorResult(false, "Setting of the forward client node is a failure");
    }

    @Override
    public ValidateResult validate(EchoServer server, ConnectionToClient client, String[] params, boolean removeForwarding) {
        if (checkIfUserEligible(client, server, params)) {
            String userId = (String) client.getInfo("loginId");
            String forwardClientId = params[1];
            if (server.isContainsClientDetails(forwardClientId) && server.getClientDetails(forwardClientId).isLoggedIn()) {
                server.getClientDetails(userId).setForwardId(null);
                return new ForwardValidatorResult(true, "Set the forward client node successfully");
            }
        }
        return new ForwardValidatorResult(false, "Setting of the forward client node is a failure");

    }

    private boolean checkIfUserEligible(Object userId, EchoServer server, String[] params) {
        boolean isEligible = userId != null
                && server.isContainsClientDetails((String) userId)
                && params != null
                && params.length >= 2
                && server.getClientDetails((String) userId).isLoggedIn();
        return isEligible;
    }

    public static class ForwardValidatorResult extends ValidateResult {

        public ForwardValidatorResult(boolean valid, String returnMsg) {
            super(valid, returnMsg);
        }
    }
}
