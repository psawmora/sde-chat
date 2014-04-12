package msc.sde.chat.util;

import msc.sde.chat.server.EchoServer;
import ocsf.server.ConnectionToClient;

/**
 * @author: prabath
 */
public interface Validator {

    ValidateResult validate(EchoServer server, ConnectionToClient client, String... prams);

}
