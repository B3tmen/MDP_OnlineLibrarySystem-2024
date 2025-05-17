package org.unibl.etf.model.book;

import org.unibl.etf.model.LibraryMember;

import java.io.Serializable;

public class BookSuggestion implements Serializable {
    private static int counter = 0;

    private int id;

    private String bookTitle;
    private String author;
    private String language;
    private boolean approved;

    public BookSuggestion() {}

    public BookSuggestion(String bookTitle, String author, String language) {
        this.id = counter++;
        this.bookTitle = bookTitle;
        this.author = author;
        this.language = language;
        this.approved = false;
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

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if (!(obj instanceof BookSuggestion)) {
            return false;
        }

        return ((BookSuggestion) obj).id == this.id;
    }
}
