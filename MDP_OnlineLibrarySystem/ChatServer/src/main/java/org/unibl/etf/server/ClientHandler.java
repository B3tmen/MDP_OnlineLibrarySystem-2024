package org.unibl.etf.server;

import com.google.gson.Gson;
import org.unibl.etf.redis.RedisChatService;
import org.unibl.etf.util.MyLogger;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;

    private RedisChatService redisChatService;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        try{
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.clientUsername = in.readLine();
            clientHandlers.add(this);
            //broadcastMessage("\n[ChatServer]: " + clientUsername + " has entered the chat.");
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }

        this.redisChatService = new RedisChatService();
    }

    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        removeClientHandler();

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

    private void broadcastMessage(String message) {
        for(ClientHandler clientHandler : clientHandlers) {
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.out.write(message);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        System.out.println("[ChatServer]: Client disconnected -> " + socket.getRemoteSocketAddress());
        //broadcastMessage("\n[ChatServer]: " + clientUsername + " has left the chat.");
    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = in.readLine();

                if (messageFromClient != null) {

                    if(messageFromClient.contains(ProtocolMessages.MSG_SENDER_RECEIVER_SEPARATOR)) {
                        String[] splitMessage = messageFromClient.split(ProtocolMessages.MSG_SENDER_RECEIVER_SEPARATOR);
                        if (splitMessage.length == 2) {
                            String recipient = splitMessage[1].split(":")[0].trim();
                            String msgToSend = clientUsername + ": " + splitMessage[1].split(":")[1].trim();

                            sendMessageToClient(recipient, msgToSend);
                        }
                        //System.out.println(messageFromClient);
                    }
                    else if(messageFromClient.startsWith(ProtocolMessages.GET_ONLINE_MEMBERS)){
                        List<String> onlineMembers = new ArrayList<>();
                        for (ClientHandler clientHandler : clientHandlers) {
                            if(!clientHandler.clientUsername.isBlank()){
                                onlineMembers.add(clientHandler.clientUsername);
                            }
                        }

                        Gson gson = new Gson();
                        String jsonList = gson.toJson(onlineMembers);
                        System.out.println("[ChatServer]: Sent jsonList - " + jsonList);
                        out.write(jsonList);
                        out.newLine();
                        out.flush();
                    }
                    else if(messageFromClient.startsWith(ProtocolMessages.GET_CHAT_HISTORY)){       // protocol is GET_CHAT_HISTORY#recipient
                        String[] parts = messageFromClient.split("#");
                        String recipient = parts[1];

                        List<Map<String, Object>> chatHistory = redisChatService.getChatHistory(this.clientUsername, recipient);

//                        List<Map<String, Object>> chatHistoryCurrentUserList = redisChatService.getChatHistory(this.clientUsername, recipient);      // Getting all messages from this user
//                        List<Map<String, Object>> chatHistoryRecipientList = redisChatService.getChatHistory(recipient, this.clientUsername);        // Getting all messages from recipient
//                        Map< String, List<Map<String, Object>> > chatHistory = new HashMap<>();    // The complete chat history with all messages, key is the username
//                        chatHistory.put(this.clientUsername, chatHistoryCurrentUserList);
//                        chatHistory.put(recipient, chatHistoryRecipientList);

                        Gson gson = new Gson();
                        String jsonMap = gson.toJson(chatHistory);
                        out.write(ProtocolMessages.RESPONSE_CHAT_HISTORY + ProtocolMessages.MSG_GENERAL_SEPARATOR + jsonMap);
                        out.newLine();
                        out.flush();
                    }
                    else if(ProtocolMessages.END.equals(messageFromClient)){
                        removeClientHandler();
                    }

                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
                break;
            }
        }
    }

    private void sendMessageToClient(String recipientUsername, String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(recipientUsername)) {
                    clientHandler.out.write(message);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();

                    redisChatService.saveChatMessage(this.clientUsername, recipientUsername, message);
                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
            }
        }
    }

    private void sendGroupChatMessage(){
        String messageFromClient;     // OLD CODE, broadcast, group chat

        while (socket.isConnected()) {
            try{

                messageFromClient = in.readLine();
                if (messageFromClient != null) {

                    broadcastMessage(messageFromClient);
                    System.out.println(messageFromClient);
                }
            } catch (IOException e){
                closeEverything(socket, in, out);
                break;
            }
        }
    }
}
