package org.unibl.etf.main;

import org.unibl.etf.model.Book;
import org.unibl.etf.rmi.ReceiptInterface;
import org.unibl.etf.rmi.ReceiptService;
import org.unibl.etf.util.ConfigReader;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws RemoteException {
        ConfigReader configReader = ConfigReader.getInstance();
        int port = configReader.getRMIPort();     // 1099
        String RMIServerName = configReader.getRMIServerName();

        ReceiptService receiptServiceObj = new ReceiptService();
        ReceiptInterface skeleton = (ReceiptInterface) UnicastRemoteObject.exportObject(receiptServiceObj, 0);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(RMIServerName, skeleton);

        System.out.println("ReceiptService RMI server object is ready.");
    }
}
