package org.unibl.etf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Receipt implements Serializable {
    private static int counter = 0;

    private int id;
    private List<BookOrder> bookOrders;
    private Date creationDate;
    private double totalPrice;

    public Receipt(List<BookOrder> bookOrders, Date creationDate, double totalPrice) {
        this.id = ++counter;
        this.bookOrders = bookOrders;
        this.creationDate = new Date();
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public List<BookOrder> getBookOrders() {
        return bookOrders;
    }
    public void setBookOrders(List<BookOrder> bookOrders) {
        this.bookOrders = bookOrders;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
