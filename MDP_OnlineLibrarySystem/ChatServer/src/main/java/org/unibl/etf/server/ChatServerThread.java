package org.unibl.etf.server;

public class ChatServerThread {
    public ChatServerThread(ClientHandler clientHandler) {
        Thread thread = new Thread(clientHandler);
        thread.start();
    }
}
