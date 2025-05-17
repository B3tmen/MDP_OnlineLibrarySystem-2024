package org.unibl.etf.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.unibl.etf.model.book.Book;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class BookDetailsController {
    private Book book;

    @javafx.fxml.FXML
    private TextArea contentTextArea;
    @javafx.fxml.FXML
    private TextField bookTitleTextField;
    @javafx.fxml.FXML
    private ImageView frontPageImageView;

    public BookDetailsController(Book book) {
        this.book = book;
    }

    @FXML
    public void initialize() {
        setupFields();
        setupFrontPageImage();
    }

    private void setupFrontPageImage() {
        if(new File(book.getFrontPage()).exists()){
            Image image = new Image(book.getFrontPage());
            frontPageImageView.setImage(image);
        }


    }

    private void setupFields() {
        bookTitleTextField.setText(book.getBookTitle());
        setupContentTextArea();
    }

    private void setupContentTextArea(){
        String[] content = book.getContent().split("\n");

        int conditionLength = Math.min(content.length, 100);
        for(int i = 0; i < conditionLength; i++){
            contentTextArea.appendText(content[i] + "\n");
        }

    }
}
