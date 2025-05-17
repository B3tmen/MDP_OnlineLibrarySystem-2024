package org.unibl.etf.util;

import java.util.Date;

public class Book {
    private String bookTitle;
    private String author;
    private Date publicationDate;
    private String language;
    private String frontPage;
    private String content;

    public Book(String bookTitle, String author, Date publicationDate, String language, String frontPage, String content) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.publicationDate = publicationDate;
        this.language = language;
        this.frontPage = frontPage;
        this.content = content;
    }

    public String getBookTitle() {
        return bookTitle;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFrontPage() {
        return frontPage;
    }
    public void setFrontPage(String frontPage) {
        this.frontPage = frontPage;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
