package org.unibl.etf.controllers;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.unibl.etf.client.ProtocolMessages;
import org.unibl.etf.client.SupplierClient;
import org.unibl.etf.model.Book;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.model.Receipt;
import org.unibl.etf.rabbitmq.OrderService;
import org.unibl.etf.rmi.BookkepingServiceRMIClient;
import org.unibl.etf.util.GuiUtil;
import org.unibl.etf.util.MyLogger;

import java.io.File;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;

public class PendingBookOrdersController {
    private static final double PRICE_LOW = 30.0;
    private static final double PRICE_HIGH = 150.0;

    private SupplierClient supplierClient;
    private BookkepingServiceRMIClient bookkeepingService;
    private ObservableList<BookOrder> bookOrders;
    private Queue<BookOrder> pendingBookOrdersQueue;

    private OrderService orderService = new OrderService();


    @javafx.fxml.FXML
    private TableView<BookOrder> bookOrdersTableView;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, Integer> orderIdColumn;
    @FXML
    private TableColumn<BookOrder, Integer> quantityColumn;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, String> frontPageColumn;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, String> authorColumn;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, String> bookTitleColumn;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, String> releaseDateColumn;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, String> isbnColumn;
    @javafx.fxml.FXML
    private TableColumn<BookOrder, String> languageColumn;
    @javafx.fxml.FXML
    private Label numberOfWaitingOrdersLabel;

    public PendingBookOrdersController(SupplierClient supplierClient){
        this.supplierClient = supplierClient;
        this.pendingBookOrdersQueue = new LinkedList<>();
        this.bookOrders = FXCollections.observableArrayList();
        try {
            this.bookkeepingService = new BookkepingServiceRMIClient();
        } catch (RemoteException | NotBoundException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        setupTableView();
        this.numberOfWaitingOrdersLabel.setText(String.valueOf(pendingBookOrdersQueue.size()));
    }

    private void setupTableView() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        frontPageColumn.setCellValueFactory(param -> {
            Book book = param.getValue().getBook();

            return new SimpleStringProperty(book.getFrontPage());
        });
        frontPageColumn.setCellFactory(param -> new TableCell<BookOrder, String>() {
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

                }
            }
        });

        isbnColumn.setCellValueFactory(param -> {
            Book book = param.getValue().getBook();

            return new SimpleStringProperty(String.valueOf(book.getIsbn()));
        });
        bookTitleColumn.setCellValueFactory(param -> {
            Book book = param.getValue().getBook();

            return new SimpleStringProperty(book.getBookTitle());
        });
        authorColumn.setCellValueFactory(param -> {
            Book book = param.getValue().getBook();

            return new SimpleStringProperty(book.getAuthor());
        });
        releaseDateColumn.setCellValueFactory(param -> {
            Book book = param.getValue().getBook();

            return new SimpleStringProperty(book.getPublicationDate().toString());
        });
        languageColumn.setCellValueFactory(param -> {
            Book book = param.getValue().getBook();

            return new SimpleStringProperty(book.getLanguage());
        });

        bookOrdersTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bookOrdersTableView.setItems(bookOrders);
    }

    public void addPendingOrder(BookOrder bookOrder){
        pendingBookOrdersQueue.add(bookOrder);

        this.numberOfWaitingOrdersLabel.setText(String.valueOf(pendingBookOrdersQueue.size()));
    }

    private void refreshTable(){
        this.bookOrdersTableView.refresh();
    }

    @javafx.fxml.FXML
    public void denyOrderAction(ActionEvent actionEvent) {
        BookOrder bookOrder = bookOrdersTableView.getSelectionModel().getSelectedItem();

        if(bookOrder != null){
            Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Order confirmation", "Are you sure you want to DENY this order? Order will be removed.", "Press 'Yes' to continue", ButtonType.YES, ButtonType.NO);
            if(answer.get() == ButtonType.YES){
                bookOrders.remove(bookOrder);
                refreshTable();
            }
        }
    }

    @javafx.fxml.FXML
    public void approveOrderAction(ActionEvent actionEvent) {
        List<BookOrder> selectedBookOrders = new ArrayList<>(bookOrdersTableView.getSelectionModel().getSelectedItems());

        if(!selectedBookOrders.isEmpty()){
            Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Order confirmation", "Are you sure you want to APPROVE these orders?", "Press 'Yes' to continue", ButtonType.YES, ButtonType.NO);
            if(answer.get() == ButtonType.YES){
                selectedBookOrders.forEach(bookOrder -> bookOrder.setApproved(true));

                double totalPrice = new Random().nextDouble(PRICE_LOW, PRICE_HIGH);
                Receipt receipt = new Receipt(new ArrayList<>(selectedBookOrders), new Date(), totalPrice);
                try {
                    double vat = bookkeepingService.generateReceipt(receipt);

                    GuiUtil.showAlert(Alert.AlertType.INFORMATION, "VAT", "Your VAT to pay for this order was: " + String.format("%.2f", vat), "Press OK to continue");

                    for(BookOrder bookOrder : selectedBookOrders){
                        sendOrderToLibrary(bookOrder.getBook());
                    }


                    this.bookOrders.removeAll(selectedBookOrders);
                    refreshTable();
                } catch (RemoteException e) {
                    MyLogger.logger.log(Level.SEVERE, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendOrderToLibrary(Book book) {
        Gson gson = new Gson();
        String json = gson.toJson(book);

        supplierClient.sendMsgToServer(ProtocolMessages.SEND_ORDER_TO_LIB + ProtocolMessages.MSG_SEPARATOR + json);
    }

    @javafx.fxml.FXML
    public void takeOrderAction(ActionEvent actionEvent) {
        int numberOfOrders = Integer.parseInt(numberOfWaitingOrdersLabel.getText());
        if(numberOfOrders > 0){
            numberOfOrders--;
            numberOfWaitingOrdersLabel.setText(String.valueOf(numberOfOrders));

            BookOrder bookOrder = pendingBookOrdersQueue.poll();
            if(bookOrder != null){
                this.bookOrders.add(bookOrder);
            }
//            else{     // can never be reached
//                GuiUtil.showAlert(Alert.AlertType.ERROR, "MQ Orders", "There are no orders to be taken at the moment", "Try again later.");
//            }
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "MQ Orders", "There are no orders to be taken at the moment", "Try again later.");
        }

    }
}
