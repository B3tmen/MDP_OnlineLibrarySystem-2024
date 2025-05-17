package org.unibl.etf.server;

import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.ConnectionParameters;
import org.unibl.etf.util.MyLogger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.logging.Level;

public class ChatServer {
    private SSLServerSocket serverSocket;
    private static final ConfigReader configReader = ConfigReader.getInstance();

    public ChatServer(SSLServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        String keyStorePath = configReader.getKeyStorePath();
        String keyStorePass = configReader.getKeyStorePassword();
//        System.setProperty("javax.net.ssl.keyStore", keyStorePath);
//        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
        int port = configReader.getChatPort();

        SSLContext sslContext = loadSSLContext(keyStorePath, keyStorePass);
        SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) socketFactory.createServerSocket(port);

        ChatServer chatServer = new ChatServer(serverSocket);
        chatServer.startChatServer();
    }

    private static SSLContext loadSSLContext(String keystorePath, String keyStorePass){
        SSLContext sslContext = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(configReader.getKeyStorePath()), keyStorePass.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, keyStorePass.toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
        }
        catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException | IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return sslContext;
    }

    public void startChatServer() {
        System.out.println("[ChatServer]: Server is up on port - " + serverSocket.getLocalPort());

        try{
            while (!serverSocket.isClosed()) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("[ChatServer]: A new client connected -> " + clientSocket.getRemoteSocketAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new ChatServerThread(clientHandler);
            }


        } catch (IOException e){
            closeServerSocket();
        }

    }


    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}
