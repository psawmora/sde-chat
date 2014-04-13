package msc.sde.chat.service;

/**
 * @author: prabath
 */
public interface Console {

    void init();

    void handleMsgFromUi(String msg);
}
