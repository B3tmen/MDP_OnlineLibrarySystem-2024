package org.unibl.etf.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.unibl.etf.model.book.Book;

import java.net.URL;
import java.util.ResourceBundle;

public class BookContentController {
    private Book book;

    @javafx.fxml.FXML
    private TextArea bookContentTextArea;

    public BookContentController(Book book) {
        this.book = book;
    }

    @FXML
    public void initialize() {
        bookContentTextArea.setText(book.getContent());
    }
}
