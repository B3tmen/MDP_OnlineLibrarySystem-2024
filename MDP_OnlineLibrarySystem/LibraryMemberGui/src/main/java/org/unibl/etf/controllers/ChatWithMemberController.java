package org.unibl.etf.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.unibl.etf.util.config.ConfigReader;
import org.unibl.etf.chat.ChatClient;
import org.unibl.etf.chat.ProtocolMessages;


import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatWithMemberController {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private ChatClient chatClient;
    private String currentUsername;
    private String recipient;


    @javafx.fxml.FXML
    private Label otherPersonUsernameLabel;
    @javafx.fxml.FXML
    private TextField messageTextField;
    @javafx.fxml.FXML
    private ScrollPane messagesContainerScrollPane;
    @javafx.fxml.FXML
    private VBox vboxMessages;
    @javafx.fxml.FXML
    private AnchorPane rootAnchorPane;

    public ChatWithMemberController(ChatClient chatClient, String recipient) {
        this.chatClient = chatClient;
        this.currentUsername = chatClient.getUsername();
        this.recipient = recipient;
        this.chatClient.setRecipient(recipient);
    }

    @FXML
    public void initialize() {
        this.chatClient.listenToMessages(vboxMessages);
        this.chatClient.setChatWithMemberController(this);
        this.chatClient.sendMessageToServer(ProtocolMessages.GET_CHAT_HISTORY, "");
        this.otherPersonUsernameLabel.setText("You are chatting with '" + recipient + "'");

        setupMessageTextFieldOnEnterPressed();

//        Stage stage = (Stage) rootAnchorPane.getScene().getWindow();
//        stage.setOnCloseRequest(event -> {
//                chatClient.sendMessageToServer(ProtocolMessages.END, "");
//            }
//        );
    }

    private void setupMessageTextFieldOnEnterPressed() {
        messageTextField.setOnKeyPressed(event ->{
            if (event.getCode() == KeyCode.ENTER) {
                String input = messageTextField.getText();

                if(!input.isEmpty()){
                    sendGuiMessageCurrentUser(input, false);
                }
            }
        });
    }


    @javafx.fxml.FXML
    public void sendMessageAction(ActionEvent actionEvent) {
        String messageToSend = messageTextField.getText();

        if(!messageToSend.isEmpty()){
            sendGuiMessageCurrentUser(messageToSend, false);
        }
    }

    public void addLabel(String messageFromClient, VBox vBox){
        sendGuiMessageRecipient(messageFromClient, vBox);
    }

    private void sendGuiMessageRecipient(String messageFromClient, VBox vBox){
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hbox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hbox);
            }
        });
    }

    private void sendGuiMessageCurrentUser(String messageToSend, boolean chatHistoryBeingRemade) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageToSend);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-color: rgb(239, 242, 255);" +
                "-fx-background-color: rgb(15, 125, 242);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        text.setFill(Color.color(0.934, 0.945, 0.996));

        hbox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vboxMessages.getChildren().add(hbox);
                messageTextField.clear();
            }
        });


        if(!chatHistoryBeingRemade){
            chatClient.sendMessageToServer(ProtocolMessages.SEND_CHAT_MSG, messageToSend);
        }


    }


    public void remakeChatHistory(List<Map<String, Object>> chatHistoryMap, VBox vbox){
        chatHistoryMap.forEach(map ->{
            String sender = (String) map.get("sender");
            String dataMsg = map.get("message").toString();
            if(sender.equals(currentUsername)){
                sendGuiMessageCurrentUser(dataMsg, true);
            }
            else{
                addLabel(dataMsg, vboxMessages);
            }
        });

//
////        List<Map<String, Object>> chatHistoryCurrentUserList = chatHistoryMap.get(currentUsername);
////        List<Map<String, Object>> chatHistoryRecipientList = chatHistoryMap.get(recipient);
//
//        chatHistoryCurrentUserList.forEach(map -> {
//            String dataMsg = map.get("message").toString();
//            long timestamp = Long.parseLong(map.get("timestamp").toString());
//
//            sendGuiMessageCurrentUser(dataMsg, true);
//        });
//
//        System.out.println("recipient list - " + chatHistoryRecipientList);
//
//        chatHistoryRecipientList.forEach(msg -> {
//            addLabel(msg, vboxMessages);
//        });
    }


}
