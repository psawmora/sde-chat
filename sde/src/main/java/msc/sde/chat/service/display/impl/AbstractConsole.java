package msc.sde.chat.service.display.impl;

import msc.sde.chat.service.ConsoleObserver;
import msc.sde.chat.service.display.ChatIF;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author: prabath
 */
public class AbstractConsole implements ChatIF {

    protected BufferedReader fromConsole;

    protected volatile boolean closeTerminal = false;

    protected ConsoleObserver observer;

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }

    public void accept() {
        try {
            System.out.print(">");
            fromConsole = new BufferedReader(new InputStreamReader(System.in));
            while (!closeTerminal) {
                System.out.print(">");
                String message = fromConsole.readLine();
                observer.handleMsgFromUi(message);
            }
        } catch (Exception ex) {
            System.out.println("Unexpected exception occurred while processing user input." + ex);
        }

    }

    @Override
    public void close() {
        System.out.println("Closing console");
        closeTerminal = true;
        if (fromConsole != null) {
            try {
                fromConsole.close();
            } catch (Throwable th) {

            }
        }
    }
}
