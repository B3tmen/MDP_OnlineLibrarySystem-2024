package org.unibl.etf.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import org.unibl.etf.model.book.Book;
import org.unibl.etf.service.BookService;
import org.unibl.etf.service.LoadUtils;
import org.unibl.etf.util.FxmlPaths;
import org.unibl.etf.util.FxmlViewManager;
import org.unibl.etf.util.GuiUtil;


import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class BooksStorageController {
    private BookService bookService;
    private ObservableList<Book> books;

    @javafx.fxml.FXML
    private TableView<Book> booksTableView;
    @javafx.fxml.FXML
    private TableColumn<Book, String> frontPageColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> isbnColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> bookTitleColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> authorColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, Date> releaseDateColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> languageColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, Void> contentColumn;
    @javafx.fxml.FXML
    private Label bookLoadStatusLabel;

    public BooksStorageController() {
        this.bookService = new BookService();
        this.books = FXCollections.observableArrayList();
        this.books.addListener((ListChangeListener.Change<? extends Book> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    bookLoadStatusLabel.setText("DONE");
                }

            }
        });

        LoadUtils.loadObservableListFromDatabase(bookService::getBooks, books);
    }

    @FXML
    public void initialize() {
        setupTableView();


    }

    private void setupTableView(){
        frontPageColumn.setCellValueFactory(new PropertyValueFactory<>("frontPage"));
        frontPageColumn.setCellFactory(param -> new TableCell<Book, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    if(new File(item).exists()){
                        Image image = new Image(item);
                        imageView.setImage(image);
                        imageView.setFitHeight(100); // Set the height of the image
                        imageView.setFitWidth(100);  // Set the width of the image
                        setGraphic(imageView);
                    }

                }
            }
        });

        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        contentColumn.setCellFactory(getCellFactoryButton("View"));

        booksTableView.setItems(books);
    }

    private Callback<TableColumn<Book, Void>, TableCell<Book, Void>> getCellFactoryButton(String buttonTitle) {
        return new Callback<>() {
            @Override
            public TableCell<Book, Void> call(final TableColumn<Book, Void> param) {
                final TableCell<Book, Void> cell = new TableCell<>() {

                    private final Button btn = new Button(buttonTitle);
                    {
                        btn.setOnAction((event) -> {
                            Book book = getTableView().getItems().get(getIndex());

                            viewBookContent(FxmlPaths.BOOK_CONTENT_VIEW, book);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
    }

    private void refreshTable() {
        this.booksTableView.refresh();
    }

    private void viewBookContent(String fxmlPath, Book book) {
        FxmlViewManager viewManager = new FxmlViewManager(fxmlPath, "Book content", new BookContentController(book));
        viewManager.showView();
    }

    private void showView(String fxmlPath, String title, Object... controller){
        FxmlViewManager viewManager = null;
        if(controller.length > 0){
            viewManager = new FxmlViewManager(fxmlPath, title, controller);
        }
        else{
            viewManager = new FxmlViewManager(fxmlPath, title);
        }

        viewManager.showView();
    }

    @javafx.fxml.FXML
    public void createBookAction(ActionEvent actionEvent) {
        showView(FxmlPaths.CREATE_BOOK_VIEW, "Create a Book", new CreateBookController(this));

    }

    @javafx.fxml.FXML
    public void updateBookAction(ActionEvent actionEvent) {
        Book book = booksTableView.getSelectionModel().getSelectedItem();

        if(book != null){
            showView(FxmlPaths.UPDATE_BOOK_VIEW, "Update a Book", new UpdateBookController(this, book));
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Book update", "You didn't select a book for updating", "Please select a book and try again.");
        }
    }

    @javafx.fxml.FXML
    public void deleteBookAction(ActionEvent actionEvent) {
        Book book = booksTableView.getSelectionModel().getSelectedItem();

        if(book != null){
            boolean changed = bookService.deleteBook(book);
            if(changed){
                boolean deleted = books.remove(book);
                if(deleted){
                    GuiUtil.showAlert(Alert.AlertType.INFORMATION, "Book deletion", "You have successfully deleted a book", "Press OK to continue.");
                }
            }
            else{
                GuiUtil.showAlert(Alert.AlertType.ERROR, "Book deletion", "You didn't select a book for deletion", "Please select a book and try again.");
            }

        }
    }

    boolean addBook(Book book){
        boolean added = bookService.addBook(book);
        if(added){
            this.books.add(book);

            refreshTable();
        }

        return added;
    }

    Book updateBook(Book book){
        Book bookUpdated = bookService.updateBook(book);
        if(bookUpdated != null){
            //int idx = books.indexOf(book);
            //books.set(idx, bookUpdated);
            refreshTable();
        }

        return bookUpdated;
    }

}
