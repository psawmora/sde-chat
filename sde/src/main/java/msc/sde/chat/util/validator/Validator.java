package msc.sde.chat.util.validator;

import msc.sde.chat.server.EchoServer;
import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public interface Validator {

    ValidateResult validate(EchoServer server, ConnectionToClient client, String... params);

    ValidateResult validate(EchoServer server, ConnectionToClient client, String[] params, boolean removeForwarding);
}
