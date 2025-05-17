package org.unibl.etf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.unibl.etf.model.book.Book;
import org.unibl.etf.util.GuiUtil;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class UpdateBookController {
    private Book book;
    private BooksStorageController booksStorageController;

    @javafx.fxml.FXML
    private TextField bookAuthorTextField;
    @javafx.fxml.FXML
    private ImageView bookFrontPageImageView;
    @javafx.fxml.FXML
    private TextField bookLanguageTextField;
    @javafx.fxml.FXML
    private TextField bookFrontPageTextField;
    @javafx.fxml.FXML
    private DatePicker releaseDateDatePicker;
    @javafx.fxml.FXML
    private TextArea bookContentTextArea;
    @javafx.fxml.FXML
    private TextField bookTitleTextField;

    public UpdateBookController(BooksStorageController booksStorageController, Book book) {
        this.booksStorageController = booksStorageController;
        this.book = book;
    }


    @FXML
    public void initialize() {
        setupFields();
        setupImage();
    }

    private void setupImage() {
        String imagePath = book.getFrontPage();

        if(new File(imagePath).exists()){
            Image image = new Image(imagePath);
            this.bookFrontPageImageView.setImage(image);
        }
    }

    private void setupFields() {
        this.bookTitleTextField.setText(book.getBookTitle());
        this.bookAuthorTextField.setText(book.getAuthor());
        this.releaseDateDatePicker.setValue(LocalDate.ofInstant(book.getPublicationDate().toInstant(), ZoneId.systemDefault()));
        this.bookLanguageTextField.setText(book.getLanguage());
        this.bookContentTextArea.setText(book.getContent());
        this.bookFrontPageTextField.setText(book.getFrontPage());
    }

    @javafx.fxml.FXML
    public void chooseFrontPageImageAction(ActionEvent actionEvent) {
        File initialDirectory = new File(System.getProperty("user.dir")); //System.getProperty("user.dir")

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please Choose an Image for the Front Page");
        if (initialDirectory.exists() && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String absolutePath = file.getAbsolutePath();
            Image image = new Image(absolutePath);

            bookFrontPageTextField.setText(absolutePath);
            bookFrontPageImageView.setImage(image);
        }
    }

    @javafx.fxml.FXML
    public void finishAddingBookAction(ActionEvent actionEvent) {
        String bookTitle = bookTitleTextField.getText();
        String author = bookAuthorTextField.getText();
        LocalDate localDate = releaseDateDatePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date releaseDate = Date.from(instant);
        String language = bookLanguageTextField.getText();
        String frontPage = bookFrontPageTextField.getText();
        String content = bookContentTextArea.getText();

        boolean isValid = checkFields(bookTitle, author, releaseDate, language, frontPage, content);

        if(isValid){
            this.book.setAll(bookTitle, author, releaseDate, language, frontPage, content);
            Book book = booksStorageController.updateBook(this.book);
            if(book != null){
                GuiUtil.showAlert(Alert.AlertType.INFORMATION, "Book update", "You have successfully updated a Book", "Press OK to continue.");
            }
        }
    }

    private boolean checkFields(String bookTitle, String author, Date releaseDate, String language, String frontPage, String content) {
        boolean isValid = true;

        if(bookTitle.isBlank() || author.isBlank() || releaseDate == null || language.isBlank() || frontPage.isBlank() || content.isBlank()){
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Book input", "You didn't enter all the necessary fields", "Please try again.");
            isValid = false;
        }


        return isValid;
    }
}
