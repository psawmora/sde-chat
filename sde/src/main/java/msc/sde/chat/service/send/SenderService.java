package msc.sde.chat.service.send;

import msc.sde.chat.server.EchoServer;

/**
 * @author: prabath
 */
public interface SenderService {

    void send(EchoServer server, String msg);
}
