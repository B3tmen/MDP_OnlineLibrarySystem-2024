package org.unibl.etf.chat;

import com.google.gson.Gson;
import javafx.scene.layout.VBox;
import org.unibl.etf.controllers.ChatWithMemberController;
import org.unibl.etf.util.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ChatClient {
    private Socket socket;
    private String username;
    private String recipient;
    private BufferedReader in;
    private BufferedWriter out;

    private ChatWithMemberController chatWithMemberController;

    public ChatClient(Socket socket, String username, String recipient) {
        this.socket = socket;
        this.username = username;
        this.recipient = recipient;
        this.chatWithMemberController = null;

        try{
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            closeEverything(socket, in, out);
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        sendInitialUsername();
    }

    public String getUsername() {
        return username;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public void setChatWithMemberController(ChatWithMemberController chatWithMemberController) {
        this.chatWithMemberController = chatWithMemberController;
    }

    private void sendInitialUsername(){
        try {
            out.write(username);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter your username: ");
//        String username = scanner.nextLine();
//        System.out.print("Enter recipient's username: ");
//        recipient = scanner.nextLine();
//
//        Socket socket = new Socket("localhost", ConnectionParameters.CHAT_PORT);
//        ChatClient chatClient = new ChatClient(socket, username);
//
//        chatClient.listenForMessages();
//        chatClient.sendMessages();
//    }

//    public void sendMessages() {
//        try {
//            out.write(username);
//            out.newLine();
//            out.flush();
//
//            Scanner scanner = new Scanner(System.in);
////            while(socket.isConnected()){
////                System.out.print("Enter message: ");
////                String dataMessage = scanner.nextLine();
////
////                out.write(username + ": " + dataMessage);
////                out.newLine();
////                out.flush();
////            }
//
//            while (socket.isConnected()) {
//                System.out.print("Enter message: ");
//                String dataMessage = scanner.nextLine();
//                System.out.println("-----------------");
//
//                sendMessageToServer(dataMessage);
//            }
//        } catch (IOException e) {
//            closeEverything(socket, in, out);
//        }
//    }

    public void sendMessageToServer(String msgType, String dataMessage){
        try{
            if(ProtocolMessages.SEND_CHAT_MSG.equals(msgType)){
                out.write(username + ProtocolMessages.MSG_SENDER_RECEIVER_SEPARATOR + recipient + ": " + dataMessage);
                out.newLine();
                out.flush();
            }
            else if(ProtocolMessages.GET_CHAT_HISTORY.equals(msgType)){
                out.write(ProtocolMessages.GET_CHAT_HISTORY + ProtocolMessages.MSG_GENERAL_SEPARATOR + recipient);    //GET_CHAT_HISTORY#recipient
                out.newLine();
                out.flush();
            }
            else if(ProtocolMessages.END.equals(msgType)){
                out.write(ProtocolMessages.END);    //GET_CHAT_HISTORY#recipient
                out.newLine();
                out.flush();

                closeEverything(socket, in, out);
            }

        } catch (IOException e){
            System.out.println("Error while sending message to server");
            closeEverything(socket, in, out);
        }
    }

    public void listenToMessages(VBox vbox) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChatServer;
                while(socket.isConnected()){
                    try {
                        msgFromChatServer = in.readLine();

                        if(msgFromChatServer.startsWith(ProtocolMessages.RESPONSE_CHAT_HISTORY)){
                            String parts[] = msgFromChatServer.split(ProtocolMessages.MSG_GENERAL_SEPARATOR);
                            String jsonMap = parts[1];
                            Gson gson = new Gson();
                            List<Map<String, Object>> chatHistory = gson.fromJson(jsonMap, List.class);
                            chatHistory.forEach(System.out::println);

                            //System.out.println("CLIENT: chat history is - " + chatHistory);

                            if(chatWithMemberController != null){
                                chatWithMemberController.remakeChatHistory(chatHistory, vbox);
                            }

                        }
                        else{
                            if(chatWithMemberController != null){
                                chatWithMemberController.addLabel(msgFromChatServer, vbox);
                            }

                        }

                        //System.out.println(msgFromChatServer);
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                        break;
                    }
                }
            }
        }).start();
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

    public void closeFromController(){
        sendMessageToServer(ProtocolMessages.END, "");
    }
}
