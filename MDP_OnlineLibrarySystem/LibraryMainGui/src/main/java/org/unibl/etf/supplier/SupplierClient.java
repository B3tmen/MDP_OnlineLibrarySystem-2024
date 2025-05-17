package org.unibl.etf.supplier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.unibl.etf.controllers.OrderBooksController;
import org.unibl.etf.model.book.Book;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

public class SupplierClient {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String host;
    private int port;

    private OrderBooksController controller;


    public SupplierClient(OrderBooksController controller) {
        try{
            this.controller = controller;

            this.host = "localhost";
            this.port = configReader.getSupplierServerPort();
            this.socket = new Socket(host, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        // Initially sending the AUTH message to identify the librarian
        sendMsgToServer(SupplierServerProtocolMessages.AUTH_LIBRARIAN);
    }

    public void listenToMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromServer;
                while(socket.isConnected()){
                    try {
                        msgFromServer = in.readLine();
                        if(msgFromServer != null){
                            if(msgFromServer.startsWith(SupplierServerProtocolMessages.ACTIVE_SUPPLIERS)){
                                String[] parts = msgFromServer.split(SupplierServerProtocolMessages.MSG_SEPARATOR);

                                int numberOfActiveSuppliers = Integer.parseInt(parts[1]);

                            }

                            else if(msgFromServer.startsWith(SupplierServerProtocolMessages.LIB_GET_AVAILABLE_BOOKS_RESPONSE)){
                                String[] parts = msgFromServer.split(SupplierServerProtocolMessages.MSG_SEPARATOR);
                                String jsonList = parts[1];

                                System.out.println("SupplierServer sent json: " + jsonList);

                                Gson gson = new Gson();
                                List<Book> books = gson.fromJson(jsonList, new TypeToken<List<Book>>() {});
                                books.forEach(Book::readContentFromFile);
                                controller.setAvailableBooks(books);
                            }

                            else if(msgFromServer.startsWith(SupplierServerProtocolMessages.LIB_SEND_BOOK_ORDER)){
                                String[] parts = msgFromServer.split(SupplierServerProtocolMessages.MSG_SEPARATOR);
                                String jsonList = parts[1];

                                Gson gson = new Gson();
                                Book book = gson.fromJson(jsonList, Book.class);
                                controller.addBookFromSupplier(book);
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

    public void sendAvailableBooksRequest(String supplierName)  {
        sendMsgToServer(SupplierServerProtocolMessages.LIB_GET_AVAILABLE_BOOKS_REQUEST + SupplierServerProtocolMessages.MSG_SEPARATOR + supplierName);

    }

    private void sendMsgToServer(String message){
        try{
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}
