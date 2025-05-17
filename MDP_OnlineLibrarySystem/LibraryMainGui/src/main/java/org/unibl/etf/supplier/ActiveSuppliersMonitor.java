package org.unibl.etf.supplier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.unibl.etf.controllers.OrderBooksController;
import org.unibl.etf.model.Supplier;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ActiveSuppliersMonitor {
    private static final ConfigReader configReader = ConfigReader.getInstance();

    private String host;
    private int port;

    private OrderBooksController controller;

    public ActiveSuppliersMonitor(OrderBooksController controller){
        this.controller = controller;
        this.host = "localhost";
        this.port = ConfigReader.getInstance().getSupplierServerPort();


        ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);
        scheduledService.scheduleAtFixedRate(this::checkAvailableSuppliers, 0, 10, TimeUnit.SECONDS);
    }

    public void checkAvailableSuppliers() {
        try (Socket socket = new Socket(host, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.sendMessageToServer(out, SupplierServerProtocolMessages.GET_ACTIVE_SUPPLIERS);

            Gson gson = new Gson();
            String jsonList = in.readLine();
            List<Supplier> suppliers = gson.fromJson(jsonList, new TypeToken<List<Supplier>>() {}.getType());   // Gson doesn't have the correct type information so we need to give it, otherwise it performs type erasure and has elements of LinkedTreeMap!
            suppliers.forEach(supplier -> {
                Platform.runLater(() -> {
                    controller.addSupplier(supplier);
                });

            });

            this.sendMessageToServer(out, SupplierServerProtocolMessages.END);

            in.close();
            out.close();
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessageToServer(BufferedWriter out, String msg){
        try{
            out.write(msg);
            out.newLine();
            out.flush();
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

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
}
