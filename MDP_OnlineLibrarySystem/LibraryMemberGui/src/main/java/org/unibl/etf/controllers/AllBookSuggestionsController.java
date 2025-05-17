package org.unibl.etf.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.unibl.etf.model.book.BookSuggestion;

import java.net.URL;
import java.util.ResourceBundle;

public class AllBookSuggestionsController {
    private ObservableList<BookSuggestion> bookSuggestions;

    @javafx.fxml.FXML
    private TableView<BookSuggestion> bookSuggestionsTableView;
    @javafx.fxml.FXML
    private TableColumn<BookSuggestion, String> approvedColumn;
    @javafx.fxml.FXML
    private TableColumn<BookSuggestion, String> titleColumn;
    @javafx.fxml.FXML
    private TableColumn<BookSuggestion, String> authorColumn;
    @javafx.fxml.FXML
    private TableColumn<BookSuggestion, String> languageColumn;

    public AllBookSuggestionsController() {
        this.bookSuggestions = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        setupTableView();

    }

    private void setupTableView() {
        approvedColumn.setCellValueFactory(param -> {
            boolean approved = param.getValue().isApproved();
            if(approved){
                return new SimpleStringProperty("YES");
            }
            else{
                return new SimpleStringProperty("NO");
            }
        });
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));

        bookSuggestionsTableView.setItems(bookSuggestions);
    }

    public void addBookSuggestion(BookSuggestion bookSuggestion) {
        if(bookSuggestions.contains(bookSuggestion)){
            int idx = bookSuggestions.indexOf(bookSuggestion);
            bookSuggestions.set(idx, bookSuggestion);
        }
        else{
            bookSuggestions.add(bookSuggestion);
        }

    }
}
