package msc.sde.chat.service.send;

import msc.sde.chat.server.EchoServer;

import java.io.IOException;

/**
 * @author: prabath
 */
public interface SenderService {

    void send(EchoServer server, String msg);

    void send(EchoServer server, String msg, String id) throws IOException;
}
