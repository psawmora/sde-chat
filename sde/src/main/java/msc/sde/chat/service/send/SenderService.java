package msc.sde.chat.service.send;

import msc.sde.chat.server.EchoServer;

import java.io.IOException;
import java.util.List;

/**
 * @author: prabath
 */
public interface SenderService {

    void send(EchoServer server, String userId, String msg) throws IOException;

    void send(EchoServer server, String msg, List<String> userIdList) throws IOException;
}
