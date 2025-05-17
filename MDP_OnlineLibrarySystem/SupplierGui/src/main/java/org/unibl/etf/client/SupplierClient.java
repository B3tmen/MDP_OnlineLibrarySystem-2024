package org.unibl.etf.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.unibl.etf.controllers.AllBooksController;
import org.unibl.etf.model.Book;
import org.unibl.etf.model.Supplier;
import org.unibl.etf.util.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

public class SupplierClient {
    private static int counter = 0;

    private Supplier supplier;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private AllBooksController controller;


    public SupplierClient(Socket socket, AllBooksController controller) {
        this.supplier = new Supplier("Supplier ");
        this.socket = socket;
        this.controller = controller;

        try{
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            closeEverything(socket, in, out);
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        sendSupplierInfo();
    }

    private void sendSupplierInfo() {
        sendMsgToServer(ProtocolMessages.SEND_SUPPLIER_INFO + ProtocolMessages.MSG_SEPARATOR + this.supplier.getSupplierName());
    }

    public void sendMsgToServer(String message) {
        try{
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e){
            closeEverything(socket, in, out);
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void listenToMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Supplier client listening...");
                String msgFromServer = "";

                while(socket.isConnected()){
                    try {
                        msgFromServer = in.readLine();

                        if(msgFromServer != null){
                            if(ProtocolMessages.GET_AVAILABLE_BOOKS.equals(msgFromServer)){
                                List<Book> books = controller.getAllBooks();

                                Gson gson = new Gson();
                                String json = gson.toJson(books);
                                System.out.println("Supplier sent: " + json);
                                byte[] bytes = json.getBytes();
                                System.out.println("bytes size: " + bytes.length);

                                sendMsgToServer(ProtocolMessages.SEND_AVAILABLE_BOOKS + ProtocolMessages.MSG_SEPARATOR + json);      // Reminder: DON'T SEND DUPLICATE FUNCTIONS !!! That was why the server didn't respond properly to librarian (function call was deleted btw)!!!
                                System.out.println("client sent data");
                            }

                            else if(msgFromServer.startsWith(ProtocolMessages.GET_SUPPLIER_BOOKS_RESPONSE)){
                                String[] parts = msgFromServer.split(ProtocolMessages.MSG_SEPARATOR);
                                String json = parts[1];

                                Gson gson = new Gson();
                                List<Book> books = gson.fromJson(json, new TypeToken<List<Book>>() {}.getType());

                                books.forEach(book -> book.readContentFromFile());
                                controller.addAllBooks(books);
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("Test run");
                        closeEverything(socket, in, out);
                        break;
                    }
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        System.out.println("Closing everything - " + socket.getLocalSocketAddress());
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
        sendMsgToServer(ProtocolMessages.END);
        closeEverything(socket, in, out);
    }
}
