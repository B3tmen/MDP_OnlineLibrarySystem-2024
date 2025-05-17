package org.unibl.etf.chat;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.shape.Circle;
import org.unibl.etf.model.LibraryMember;
import org.unibl.etf.util.config.ConfigReader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatServerStatusMonitor {

    private final Circle statusIndicator;
    private final Label statusLabel;
    private final TableView<String> memberTableView;
    private String host;
    private int port;

    private ScheduledExecutorService scheduledService;

    public ChatServerStatusMonitor(Circle statusIndicatorCircle, Label statusLabel, TableView<String> memberTableView) {
        ConfigReader configReader = ConfigReader.getInstance();

        this.statusIndicator = statusIndicatorCircle;
        this.statusLabel = statusLabel;
        this.memberTableView = memberTableView;
        this.host = "localhost";
        this.port = configReader.getChatPort();

        scheduledService = Executors.newScheduledThreadPool(1);
        scheduledService.scheduleAtFixedRate(this::checkServerStatus, 0, 5, TimeUnit.SECONDS);
    }

    private void checkServerStatus() {
        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket.connect(socketAddress, 1000); // Timeout 1s for connecting

            updateStatus("ONLINE", "#26de81", false);      // #26de81 green
        } catch (IOException e) {
            updateStatus("OFFLINE", "#ff3f34", true);     // #ff3f34 red
        }
    }

    private void updateStatus(String status, String color, boolean tableStatusDisabled) {
        Platform.runLater(() -> {
            statusIndicator.setStyle("-fx-fill: " + color + ";" +
                                    "-fx-stroke: " + color + ";");
            statusLabel.setText(status);
            memberTableView.setDisable(tableStatusDisabled);
        });
    }

    public void closeService(){
        scheduledService.shutdown();
    }
}
