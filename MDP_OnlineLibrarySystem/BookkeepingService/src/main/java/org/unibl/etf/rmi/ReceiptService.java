package org.unibl.etf.rmi;

import org.unibl.etf.model.Book;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.model.Receipt;
import org.unibl.etf.util.ReceiptWriter;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ReceiptService implements ReceiptInterface {
    public static final double VAT_RATE = 0.17;
    private static final double PRICE_LOW = 30.0;
    private static final double PRICE_HIGH = 150.0;

    public ReceiptService(){

    }

    @Override
    public double generateReceipt(Receipt receipt) throws RemoteException {
        double vat = calculateVAT(receipt.getTotalPrice());

        ReceiptWriter.writeReceiptToFile(receipt, vat);

        return vat;
    }

    @Override
    public double calculateVAT(double price) throws RemoteException {
        return VAT_RATE * price;
    }
}
