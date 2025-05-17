package org.unibl.etf.model;

import java.util.List;

public class EmailRequestDTO {
    private List<Book> orderedBooks;
    private String toEmail;
    private String attachmentPath;
    private String attachmentName;

    public EmailRequestDTO() {

    }

    public EmailRequestDTO(List<Book> orderedBooks, String toEmail, String attachmentPath, String attachmentName) {
        this.orderedBooks = orderedBooks;
        this.toEmail = toEmail;
        this.attachmentPath = attachmentPath;
        this.attachmentName = attachmentName;
    }

    public List<Book> getOrderedBooks() {
        return orderedBooks;
    }
    public void setOrderedBooks(List<Book> orderedBooks) {
        this.orderedBooks = orderedBooks;
    }

    public String getToEmail() {
        return toEmail;
    }
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentName() {
        return attachmentName;
    }
    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

}
