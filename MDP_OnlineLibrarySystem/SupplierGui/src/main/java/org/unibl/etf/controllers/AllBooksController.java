package org.unibl.etf.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.unibl.etf.model.Book;
import org.unibl.etf.util.FxmlPaths;
import org.unibl.etf.util.FxmlViewManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AllBooksController {
    private ObservableList<Book> allBooks;

    @javafx.fxml.FXML
    private TableView<Book> booksTableView;
    @javafx.fxml.FXML
    private TableColumn<Book, String> frontPageColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> authorColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> bookTitleColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> releaseDateColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, Void> contentColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> isbnColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> languageColumn;

    public AllBooksController() {
        this.allBooks = FXCollections.observableArrayList();    //redisBookService.getAllBooks()

        //addTestBooks();
    }

    private void addTestBooks() {
        allBooks.add(new Book("t1", "a1", new Date(), "l1", "fp1", "c1"));
        allBooks.add(new Book("t2", "a2", new Date(), "l2", "fp2", "c2"));
        allBooks.add(new Book("t3", "a3", new Date(), "l3", "fp3", "c3"));
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
                    File file = new File(item);
                    if(file.exists()){
                        Image image = new Image(item);
                        imageView.setImage(image);
                        imageView.setFitHeight(100); // Set the height of the image
                        imageView.setFitWidth(100);  // Set the width of the image
                        setGraphic(imageView);
                    }
                    else{

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

        booksTableView.setItems(allBooks);
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

    public List<Book> getAllBooks(){
        List<Book> books = new ArrayList<>();
        books.addAll(allBooks);

        return books;
    }

    public void addAllBooks(List<Book> allBooks) {
        this.allBooks.addAll(allBooks);
    }

    private void viewBookContent(String fxmlPath, Book book) {
        FxmlViewManager viewManager = new FxmlViewManager(fxmlPath, "Book content", new BookContentController(book));
        viewManager.showView();
    }

}
