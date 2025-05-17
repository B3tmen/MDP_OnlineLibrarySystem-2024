package org.unibl.etf.controllers;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
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
import org.unibl.etf.model.book.BookOrder;
import org.unibl.etf.model.Supplier;
import org.unibl.etf.rabbitmq.MessagePublisher;
import org.unibl.etf.service.BookService;
import org.unibl.etf.supplier.ActiveSuppliersMonitor;
import org.unibl.etf.supplier.SupplierClient;
import org.unibl.etf.util.FxmlPaths;
import org.unibl.etf.util.FxmlViewManager;
import org.unibl.etf.util.GuiUtil;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class OrderBooksController {
    private MessagePublisher messagePublisher;
    private BookService bookService;
    private SupplierClient supplierClient;

    private ObservableList<Book> availableBooks;
    private ObservableList<String> suppliers;

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
    private ComboBox<String> suppliersComboBox;
    @FXML
    private TextField quantityTextField;


    public OrderBooksController(){
        this.messagePublisher = new MessagePublisher();
        this.bookService = new BookService();
        this.availableBooks = FXCollections.observableArrayList();
        this.suppliers = FXCollections.observableArrayList();

    }

    @FXML
    public void initialize() {
        this.supplierClient = new SupplierClient(this);
        this.supplierClient.listenToMessages();
        new ActiveSuppliersMonitor(this);

        setupTableView();
        setupComboBox();
    }

    public void addSupplier(Supplier supplier){
        if(!suppliers.contains(supplier.getSupplierName())){
            this.suppliers.add(supplier.getSupplierName());
        }
    }

    private void setupComboBox() {
        this.suppliersComboBox.setItems(suppliers);
        this.suppliersComboBox.setOnAction(this::sendRequestForAvailableBooksFromSupplier);

    }

    private void sendRequestForAvailableBooksFromSupplier(ActionEvent event) {
        String supplierName = suppliersComboBox.getValue();
        if(supplierName != null){
            System.out.println("Sent request from Gui");
            this.supplierClient.sendAvailableBooksRequest(supplierName);
        }

    }

    private void setupTableView(){
        //this.availableBooks.add(new Book("t1", "a1", new Date(), "l1", "fp1", "c1"));

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
                    else{   //TODO: book orders, book image what if missing?

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

        booksTableView.setItems(availableBooks);
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

    private void viewBookContent(String fxmlPath, Book book) {
        FxmlViewManager viewManager = new FxmlViewManager(fxmlPath, "Book content", new BookContentController(book));
        viewManager.showView();
    }



    @javafx.fxml.FXML
    public void orderBookAction(ActionEvent actionEvent) {
        String comboBoxItem = suppliersComboBox.getValue();
        if(comboBoxItem != null){
            Book book = booksTableView.getSelectionModel().getSelectedItem();
            if(book != null){
                String quantityStr = quantityTextField.getText();
                if(!quantityStr.isBlank()){
                    boolean matchesDigits = quantityStr.matches("\\d+");
                    if(matchesDigits){
                        int quantity = Integer.parseInt(quantityStr);
                        Gson gson = new Gson();
                        String jsonOrder = gson.toJson(new BookOrder(book, quantity));
                        messagePublisher.publishMessage(jsonOrder);

                        GuiUtil.showAlert(Alert.AlertType.INFORMATION, "Order sent", "Your order has been placed", "A supplier will be notified of your order");
                    }
                    else{
                        GuiUtil.showAlert(Alert.AlertType.ERROR, "Order Placement", "The quantity should contain only digits", "Please enter a number");
                    }
                }
                else{
                    GuiUtil.showAlert(Alert.AlertType.ERROR, "Order Placement", "You didn't enter the quantity of books for Your order", "Please enter a number");
                }
            }
            else{
                GuiUtil.showAlert(Alert.AlertType.ERROR, "Order Selection", "You didn't select a book for Your order", "Please select one");
            }
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Supplier Selection", "You didn't select an appropriate Supplier", "Please select one");
        }
    }

    public void setAvailableBooks(List<Book> books) {
        this.availableBooks.setAll(books);
    }

    public void addBookFromSupplier(Book book){
        bookService.addBook(book);
    }
}
