package msc.sde.chat.service;

/**
 * @author: prabath
 */
public interface ConsoleObserver {

    void init();

    void handleMsgFromUi(String msg);
}
