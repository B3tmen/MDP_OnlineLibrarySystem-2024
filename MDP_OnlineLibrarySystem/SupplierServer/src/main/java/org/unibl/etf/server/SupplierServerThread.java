package org.unibl.etf.server;

import java.io.*;
import java.net.Socket;

public class SupplierServerThread {

    public SupplierServerThread(ClientHandler clientHandler) {
        Thread thread = new Thread(clientHandler);
        thread.start();
    }

}
