package org.unibl.etf.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import org.unibl.etf.model.LibraryMember;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.config.ConfigReader;
import org.unibl.etf.util.gui.FxmlPaths;
import org.unibl.etf.util.gui.FxmlViewManager;
import org.unibl.etf.chat.ChatClient;
import org.unibl.etf.chat.ChatOnlineMembersMonitor;
import org.unibl.etf.chat.ChatServerStatusMonitor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ChatMainController {
    private static final ConfigReader configReader = ConfigReader.getInstance();

    private ObservableList<String> members;
    private LibraryMember currentUser;

    private ChatClient chatClient;
    private ChatServerStatusMonitor chatServerStatusMonitor;
    private ChatOnlineMembersMonitor chatOnlineMembersMonitor;

    @javafx.fxml.FXML
    private TableView<String> membersTableView;
    @javafx.fxml.FXML
    private TableColumn<String, String> usernameColumn;
    @javafx.fxml.FXML
    private TableColumn<String, Void> communicateButtonColumn;
    @javafx.fxml.FXML
    private Circle onlineStatusCircle;
    @javafx.fxml.FXML
    private Label serverStatusLabel;

    public ChatMainController(LibraryMember currentUser) {
        this.currentUser = currentUser;
        this.members = FXCollections.observableArrayList();


        //connectToChatServer();
    }

    private void connectToChatServer(){
        int port = configReader.getChatPort();
        String trustStorePath = configReader.getTrustStorePath();
        String trustStorePassword = configReader.getTrustStorePassword();

        SSLContext sslContext = loadSSLContext(trustStorePath, trustStorePassword);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = null;
        try {
            sslSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", port);
            this.chatClient = new ChatClient(sslSocket, this.currentUser.getUsername(), "");
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
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
        catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException |
               IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return sslContext;
    }

    @FXML
    public void initialize() {
        connectToChatServer();
        //addMembersTest();
        setupTableView();

        this.chatOnlineMembersMonitor = new ChatOnlineMembersMonitor(members);
        this.chatServerStatusMonitor = new ChatServerStatusMonitor(onlineStatusCircle, serverStatusLabel, membersTableView);
    }

    private void addMembersTest() {
        //members.add(currentUser);
        if(currentUser.getUsername().equals("aco")){
            members.add("ivan");
        }
        else if(currentUser.getUsername().equals("ivan")){
            members.add("aco");
        }

        //members.add(new LibraryMember("aco", "aa", "aleksa", "blagojevic", "adress2", "mail2.com"));
        //members.add(new LibraryMember("ivan", "aa", "ivan", "kuruzovic", "adress2", "mail2.com"));
    }

    private void setupTableView() {
        this.usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        this.communicateButtonColumn.setCellFactory(getCellFactoryButton("Start chat"));

        // Set row factory to disable specific rows (the row with the username of the current user - he can't chat with himself!)
        membersTableView.setRowFactory(new Callback<TableView<String>, TableRow<String>>() {
            @Override
            public TableRow<String> call(TableView<String> tableView) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(String username, boolean empty) {
                        super.updateItem(username, empty);

                        if (username != null && username.equals(currentUser.getUsername())) {
                            setDisable(true);
                        } else {
                            setDisable(false);
                        }
                    }
                };
            }
        });

        this.membersTableView.setItems(members);
    }


    private Callback<TableColumn<String, Void>, TableCell<String, Void>> getCellFactoryButton(String buttonTitle) {
        return new Callback<>() {
            @Override
            public TableCell<String, Void> call(final TableColumn<String, Void> param) {
                final TableCell<String, Void> cell = new TableCell<>() {

                    private final Button btn = new Button(buttonTitle);
                    {
                        btn.setOnAction((event) -> {
                            String otherMember = getTableView().getItems().get(getIndex());

                            startChat(otherMember);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
    }

    private void startChat(String recipient){
        FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.CHAT_WITH_MEMBER_VIEW, "Library Member", new ChatWithMemberController(chatClient, recipient));
        viewManager.showView();
    }

    public void closeEverything(){
        if(chatOnlineMembersMonitor != null){
            this.chatOnlineMembersMonitor.closeService();
        }
        if(chatServerStatusMonitor != null){
            this.chatServerStatusMonitor.closeService();
        }
        if(chatClient != null){
            this.chatClient.closeFromController();
        }

    }
}
