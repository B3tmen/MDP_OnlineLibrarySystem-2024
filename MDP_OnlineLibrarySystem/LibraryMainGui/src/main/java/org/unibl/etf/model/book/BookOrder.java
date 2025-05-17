package org.unibl.etf.model.book;

public class BookOrder {
    private static int counter = 0;
    
    private int id;
    private Book book;
    private boolean approved;
    private int quantity;

    public BookOrder() {}

    public BookOrder(Book book, int quantity) {
        this.id = counter++;
        this.book = book;
        this.approved = false;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
