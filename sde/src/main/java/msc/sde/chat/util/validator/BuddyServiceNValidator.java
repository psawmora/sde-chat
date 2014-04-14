package msc.sde.chat.util.validator;

import msc.sde.chat.server.EchoServer;
import msc.sde.chat.util.ClientDetails;
import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public class BuddyServiceNValidator {

    public ValidateResult register(EchoServer server, ConnectionToClient client, String... params) {
        if (params != null && params.length >= 2) {
            String userId = (String) client.getInfo("loginId");
            String buddyId = params[1];
            ClientDetails buddyDetails = server.getClientDetails(buddyId);
            if (buddyDetails != null) {
                buddyDetails.addToBuddyList(userId);
                return new BuddyValidatorResult(true, "Adding to buddy list is success");
            }
        }
        return new BuddyValidatorResult(false, "Adding to buddy list failed");
    }

    public ValidateResult deRegister(EchoServer server, ConnectionToClient client, String[] params) {
        if (params != null && params.length >= 2) {
            String userId = (String) client.getInfo("loginId");
            String buddyId = params[1];
            ClientDetails buddyDetails = server.getClientDetails(buddyId);
            if (buddyDetails != null) {
                buddyDetails.removeFromBuddyList(userId);
                return new BuddyValidatorResult(true, "Removing from buddy list is success");
            }
        }
        return new BuddyValidatorResult(false, "Removing from buddy list failed");

    }

    public static class BuddyValidatorResult extends ValidateResult {

        public BuddyValidatorResult(boolean valid, String returnMsg) {
            super(valid, returnMsg);
        }
    }
}
