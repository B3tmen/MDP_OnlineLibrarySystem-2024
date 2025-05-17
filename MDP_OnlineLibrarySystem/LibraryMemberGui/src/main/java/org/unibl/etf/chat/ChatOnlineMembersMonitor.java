package org.unibl.etf.chat;

import com.google.gson.Gson;
import javafx.collections.ObservableList;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.config.ConfigReader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ChatOnlineMembersMonitor {
    private static final ConfigReader configReader = ConfigReader.getInstance();

    private ObservableList<String> members;
    private String host;
    private int port;
    private BufferedReader in;
    private BufferedWriter out;

    private ScheduledExecutorService scheduledService;

    public ChatOnlineMembersMonitor(ObservableList<String> members){
        this.members = members;
        this.host = "localhost";
        this.port = ConfigReader.getInstance().getChatPort();


        scheduledService = Executors.newScheduledThreadPool(1);
        scheduledService.scheduleAtFixedRate(this::checkOnlineMembers, 0, 5, TimeUnit.SECONDS);
    }

    private void checkOnlineMembers() {
        String trustStorePath = configReader.getTrustStorePath();
        String trustStorePassword = configReader.getTrustStorePassword();

        SSLContext sslContext = loadSSLContext(trustStorePath, trustStorePassword);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(host, port)) {
            //System.out.println("socket params: " + socket.getLocalSocketAddress());
            //SocketAddress socketAddress = new InetSocketAddress(host, port);
            //socket.connect(socketAddress, 1000); // Timeout 1s for connecting
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write("");  // The first thing the server will read is the "username", so we have to "skip" it
            out.newLine();
            out.flush();

            //System.out.println("Sending " + ProtocolMessages.GET_ONLINE_MEMBERS);
            out.write(ProtocolMessages.GET_ONLINE_MEMBERS);
            out.newLine();
            out.flush();

            Gson gson = new Gson();
            String jsonList = in.readLine();
            List<String> usernames = gson.fromJson(jsonList, ArrayList.class);
            members.setAll(usernames);

            out.write(ProtocolMessages.END);
            out.newLine();
            out.flush();

            closeEverything(socket, in, out);
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            //e.printStackTrace();
        }
    }

    private SSLContext loadSSLContext(String trustStorePath, String trustStorePassword) {
        SSLContext sslContext = null;
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(trustStorePath), trustStorePassword.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(trustStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        }
        catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException | IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return sslContext;
    }


    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeService(){
        scheduledService.shutdown();
    }
}
