package org.unibl.etf.rmi;

import org.unibl.etf.model.Book;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.model.Receipt;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ReceiptInterface extends Remote {
    double generateReceipt(Receipt receipt) throws RemoteException;
    double calculateVAT(double price) throws RemoteException;   // VAT -> Value Added Tax (english) == PDV -> Porez na Dodatu Vrijednost (serbian)
}
