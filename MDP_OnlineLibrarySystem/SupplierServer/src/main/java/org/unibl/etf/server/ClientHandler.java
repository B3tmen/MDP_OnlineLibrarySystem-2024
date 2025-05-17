package org.unibl.etf.server;

import com.google.gson.Gson;
import org.unibl.etf.model.Book;
import org.unibl.etf.model.Supplier;
import org.unibl.etf.temp.RedisBookService;
import org.unibl.etf.util.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private static Socket libSocket = null;
    private static BufferedReader libIn = null;
    private static BufferedWriter libOut = null;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private Supplier supplier;
    private RedisBookService redisBookService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.supplier = null;
        this.redisBookService = new RedisBookService();

        try{
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
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

    public void removeClientHandler(){
        System.out.println("[SupplierServer]: Client disconnected from the server -> " + socket.getRemoteSocketAddress());

        clientHandlers.remove(this);
    }


    @Override
    public void run() {
        System.out.println("\n[SupplierServer]: Client connected -> " + socket.getRemoteSocketAddress());

        String clientRequest;
        while (socket.isConnected()) {
            try {
                clientRequest = in.readLine();

                if (clientRequest != null) {
                    if(ProtocolMessages.AUTH_LIBRARIAN.equals(clientRequest)){
                        System.out.println("[SupplierServer]: Librarian connected to the server -> " + socket.getRemoteSocketAddress());
                        libSocket = socket;
                        libIn = in;
                        libOut = out;
                    }

                    else if(ProtocolMessages.GET_SUPPLIER_BOOKS_REQUEST.equals(clientRequest)){
                        List<Book> booksToSend = redisBookService.getAllBooks();

                        Gson gson = new Gson();
                        String json = gson.toJson(booksToSend);
                        String msg = ProtocolMessages.GET_SUPPLIER_BOOKS_RESPONSE + ProtocolMessages.MSG_SEPARATOR + json;

                        sendMessageToClient(out, msg);
                    }

                    else if(clientRequest.startsWith(ProtocolMessages.SEND_SUPPLIER_INFO)){
                        String[] parts = clientRequest.split(ProtocolMessages.MSG_SEPARATOR);
                        String supplierName = parts[1];

                        System.out.println("[SupplierServer]: Supplier connected to the server -> " + supplierName + ", socket: " + socket.getRemoteSocketAddress());
                        this.supplier = new Supplier(supplierName + (clientHandlers.size()+1));
                        clientHandlers.add(this);
                    }

                    else if(ProtocolMessages.GET_ACTIVE_SUPPLIERS.equals(clientRequest)){
                        List<Supplier> activeSuppliers = new ArrayList<>();
                        for (ClientHandler clientHandler : clientHandlers) {
                            if(clientHandler.supplier != null){
                                activeSuppliers.add(clientHandler.supplier);
                            }
                        }

                        Gson gson = new Gson();
                        String jsonList = gson.toJson(activeSuppliers);
                        System.out.println("Server sent json list: " + jsonList);
                        out.write(jsonList);
                        out.newLine();
                        out.flush();
                    }

                    else if(clientRequest.startsWith(ProtocolMessages.LIB_GET_AVAILABLE_BOOKS_REQUEST)){
                        String[] parts = clientRequest.split(ProtocolMessages.MSG_SEPARATOR);
                        String supplierName = parts[1];


                        for(ClientHandler clientHandler : clientHandlers){
                            if(clientHandler.supplier != null && clientHandler.supplier.getSupplierName().equals(supplierName)){
                                sendMessageToClient(clientHandler.out, ProtocolMessages.GET_AVAILABLE_BOOKS);   // we have to send the request to the appropriate supplier
                            }
                        }
                    }

                    else if(clientRequest.startsWith(ProtocolMessages.SEND_AVAILABLE_BOOKS)){
                        String[] parts = clientRequest.split(ProtocolMessages.MSG_SEPARATOR);
                        String jsonList = parts[1];
                        System.out.println("Supplier responded with: " + jsonList + ", sending response to: " + libSocket.getRemoteSocketAddress());

                        // We need to send it to the librarian socket, hence why it's static
                        sendMessageToClient(libOut, ProtocolMessages.LIB_GET_AVAILABLE_BOOKS_RESPONSE + ProtocolMessages.MSG_SEPARATOR + jsonList);
                    }

                    else if(clientRequest.startsWith(ProtocolMessages.SEND_ORDER_TO_LIB)){
                        String[] parts = clientRequest.split(ProtocolMessages.MSG_SEPARATOR);
                        String jsonList = parts[1];
                        System.out.println("Supplier sent order to lib: " + jsonList + ", sending response to: " + libSocket.getRemoteSocketAddress());

                        sendMessageToClient(libOut, ProtocolMessages.LIB_SEND_BOOK_ORDER + ProtocolMessages.MSG_SEPARATOR + jsonList);
                    }

                    else if (ProtocolMessages.END.equals(clientRequest)) {
                        removeClientHandler();
                        closeEverything(socket, in, out);
                    }

                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
                break;
            }
        }

    }

    private void sendMessageToClient(BufferedWriter out, String message) throws IOException {
        out.write(message);
        out.newLine();
        out.flush();
    }

}
