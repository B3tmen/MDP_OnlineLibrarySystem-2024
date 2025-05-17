package org.unibl.etf.model.book;


import org.unibl.etf.util.ISBNGenerator;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.config.ConfigReader;

import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;

public class Book {
    private static final String CONTENTS_PATH = ConfigReader.getInstance().getBookContentsPath();
    private static int counter = 0;

    private String isbn;
    private String bookTitle;
    private String author;
    private Date publicationDate;
    private String language;
    private String frontPage;
    private transient String content;
    private String contentFilePath;

    public Book() {

    }

    public Book(String bookTitle, String author, Date publicationDate, String language, String frontPage, String content) {
        this.isbn = ISBNGenerator.generateSimulatedISBN();
        this.bookTitle = bookTitle;
        this.author = author;
        this.publicationDate = publicationDate;
        this.language = language;
        this.frontPage = frontPage;
        this.content = content;
        this.contentFilePath = Paths.get(CONTENTS_PATH).toAbsolutePath() + File.separator + "Book_" + isbn + ".txt";

        if(this.content != null){
            writeContentToFile();
        }
        else{
            readContentFromFile();
        }
    }

    private void writeContentToFile() {
        try {
            File file = new File(contentFilePath);
            if(!file.exists()){
                file.createNewFile();
            }

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
            writer.println(content);

            writer.close();
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void readContentFromFile() {
        //this.contentFilePath = Paths.get(CONTENTS_PATH).toAbsolutePath() + File.separator + "Book_" + isbn + ".txt";
        this.content = "";
        try{
            File file = new File(this.contentFilePath);
            if(file.exists()){
                String line = "";
                BufferedReader reader = new BufferedReader(new FileReader(file));

                while((line = reader.readLine()) != null){
                    this.content += line + "\n";
                }
            }

        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public String getContentFilePath() {
        return contentFilePath;
    }
    public void setContentFilePath(String contentFilePath) {
        this.contentFilePath = contentFilePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Book)) {
            return false;
        }

        return this.isbn.equals( ((Book) obj).getIsbn() );
    }


    @Override
    public String toString() {
        return "Book [isbn=" + isbn + ", bookTitle=" + bookTitle + ", author=" + author + ", publicationDate=" + publicationDate + ", language=" + language + ", frontPage=" + frontPage +  "]";
    }
}
