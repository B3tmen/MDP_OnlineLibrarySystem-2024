package org.unibl.etf.multicast;

import org.unibl.etf.controllers.AllBookSuggestionsController;
import org.unibl.etf.model.book.BookSuggestion;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.config.ConfigReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;

public class MulticastServer extends Thread {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private static final String MULTICAST_GROUP = configReader.getMulticastGroup();
    private static final int MULTICAST_PORT = configReader.getMulticastPort();

    private AllBookSuggestionsController controller;

    private boolean isRunning = true;

    public MulticastServer(AllBookSuggestionsController controller){
        this.controller = controller;

        //this.setDaemon(true);
        this.start();
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }


    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)){
            System.out.println("[MulticastServer]: Multicast Server started...");


            InetAddress address = InetAddress.getByName(MULTICAST_GROUP);
            socket.joinGroup(address);
            while(isRunning){
                byte[] receivedData = new byte[256];
                DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
                socket.receive(packet);

                byte[] objectBytes = packet.getData();
                ByteArrayInputStream bais = new ByteArrayInputStream(objectBytes);
                ObjectInputStream ois = new ObjectInputStream(bais);

                Object obj = ois.readObject();
                if(obj instanceof BookSuggestion bookSuggestion){
                    if(!bookSuggestion.isApproved()){
                        sendToLibraryMulticastServer(bookSuggestion);
                    }

                    controller.addBookSuggestion(bookSuggestion);
                }

//                if (isRunning == false) {       // Stopping the server with an Interrupt call from LibraryMemberMainController after logout
//                    System.out.println("[MulticastServer]: Multicast Server terminated...");
//                    bais.close();
//                    ois.close();
//                    break;
//                }

                bais.close();
                ois.close();
            }

            System.out.println("[MulticastServer]: Multicast Server terminated...");
            socket.leaveGroup(address);
        } catch (IOException | ClassNotFoundException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendToLibraryMulticastServer(BookSuggestion bookSuggestion){
        MulticastPublisher publisher = new MulticastPublisher(bookSuggestion, configReader.getMulticastLibraryPort());

        publisher.sendMulticastMsg();
    }

}
