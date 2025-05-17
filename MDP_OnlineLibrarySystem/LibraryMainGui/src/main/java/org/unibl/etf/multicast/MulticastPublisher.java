package org.unibl.etf.multicast;

import org.unibl.etf.model.book.BookSuggestion;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;

public class MulticastPublisher {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private static final String MULTICAST_GROUP = configReader.getMulticastGroup();

    private BookSuggestion bookSuggestion;
    private ByteArrayOutputStream baos;
    private ObjectOutputStream oos;

    private int port;


    public MulticastPublisher(BookSuggestion bookSuggestion, int port) {
        this.bookSuggestion = bookSuggestion;
        this.baos = new ByteArrayOutputStream();
        this.port = port;

        try{
            oos = new ObjectOutputStream(baos);
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }


    }

    public void sendMulticastMsg(){
        try (MulticastSocket socket = new MulticastSocket()){
            InetAddress address = InetAddress.getByName(MULTICAST_GROUP);
            socket.joinGroup(address);

            oos.writeObject(bookSuggestion);
            byte[] objectBytes = baos.toByteArray();

            System.out.println("bytes length: " + objectBytes.length);

            DatagramPacket packet = new DatagramPacket(objectBytes, objectBytes.length, address, port);
            socket.send(packet);
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

    }
}
