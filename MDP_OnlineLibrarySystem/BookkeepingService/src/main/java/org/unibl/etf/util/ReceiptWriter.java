package org.unibl.etf.util;

import org.unibl.etf.model.Book;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.model.Receipt;
import org.unibl.etf.rmi.ReceiptService;

import java.io.*;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ReceiptWriter {
    private static final String RECEIPTS_PATH = ConfigReader.getInstance().getReceiptsPath();

    public static void writeReceiptToFile(Receipt receipt, double vat){
        try{
            String filename = RECEIPTS_PATH + "Receipt_" + receipt.getId() + ".txt";
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
            String receiptMessage = "";

            receiptMessage += "=================================== GENERATED RECEIPT FOR ORDER ===================================\n\n";
            receiptMessage += "Ordered books:\n";
            receiptMessage += String.format("%-10s %-30s %-30s %-40s %-45s %-40s\n", "Quantity", "ISBN", "Book title", "Author", "Release date", "Language");
            for(BookOrder bookOrder : receipt.getBookOrders()) {
                Book book = bookOrder.getBook();
                receiptMessage += String.format("%-10s %-30s %-30s %-40s %-45s %-40s\n", bookOrder.getQuantity(), book.getIsbn(), book.getBookTitle(), book.getAuthor(), book.getPublicationDate().toString(), book.getLanguage());
            }
            receiptMessage += "\nTOTAL PRICE: " + String.format("%.2f", receipt.getTotalPrice());
            receiptMessage += "\nCreated on: " + receipt.getCreationDate();
            receiptMessage += "\nVAT to pay ("+ ReceiptService.VAT_RATE * 100 + "%): " + String.format("%.2f", vat) + "\n";

            writer.println(receiptMessage);
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}
