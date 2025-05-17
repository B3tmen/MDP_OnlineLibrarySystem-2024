package org.unibl.etf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.unibl.etf.model.book.BookSuggestion;
import org.unibl.etf.multicast.MulticastPublisher;
import org.unibl.etf.util.config.ConfigReader;
import org.unibl.etf.util.gui.GuiUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class WriteBookSuggestionController {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private BookSuggestion bookSuggestion;

    @javafx.fxml.FXML
    private TextField languageTextField;
    @javafx.fxml.FXML
    private TextField bookTitleTextField;
    @javafx.fxml.FXML
    private TextField authorTextField;


    @FXML
    public void initialize() {

    }

    @javafx.fxml.FXML
    public void finishBookSuggestionAction(ActionEvent actionEvent) {
        String title = bookTitleTextField.getText();
        String author = authorTextField.getText();
        String language = languageTextField.getText();

        boolean valid = checkFields(title, author, language);

        if(valid){
            bookSuggestion = new BookSuggestion(title, author, language);
            MulticastPublisher publisher = new MulticastPublisher(bookSuggestion, configReader.getMulticastPort());
            publisher.sendMulticastMsg();

            GuiUtil.showAlert(Alert.AlertType.INFORMATION, "Book Suggestion", "Your book suggestion was sent.", "You can view all book suggestions in the 'See Book Suggestions' section");
        }
    }

    private boolean checkFields(String title, String author, String language) {
        boolean valid = true;

        if(title.isBlank() || author.isBlank() || language.isBlank()) {
            valid = false;

            GuiUtil.showAlert(Alert.AlertType.ERROR, "ERROR", "Missing fields with data", "Please fill out all the fields");
        }


        return valid;
    }
}
