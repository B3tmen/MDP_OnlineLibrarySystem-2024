package org.unibl.etf.rmi;

import org.unibl.etf.model.Book;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.model.Receipt;
import org.unibl.etf.util.ConfigReader;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BookkepingServiceRMIClient {
    private static ConfigReader configReader = ConfigReader.getInstance();

    private ReceiptInterface stub;
    private Registry registry;

    public BookkepingServiceRMIClient() throws RemoteException, NotBoundException {
        connectToServer();

    }

    public void connectToServer() throws RemoteException, NotBoundException {
        this.registry = LocateRegistry.getRegistry();

        String name = configReader.getRMIServerName();
        this.stub = (ReceiptInterface) registry.lookup(name);
    }

    public double generateReceipt(Receipt receipt) throws RemoteException {
        return stub.generateReceipt(receipt);
    }

    public double calculateVAT(double price) throws RemoteException {
        return stub.calculateVAT(price);
    }
}
