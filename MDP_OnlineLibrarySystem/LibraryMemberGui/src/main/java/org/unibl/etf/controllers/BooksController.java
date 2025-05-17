package org.unibl.etf.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.unibl.etf.model.book.Book;
import org.unibl.etf.model.EmailRequestDTO;
import org.unibl.etf.redis.RedisBookService;
import org.unibl.etf.service.MemberService;
import org.unibl.etf.service.RequestSenderUtil;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.gui.FxmlPaths;
import org.unibl.etf.util.gui.FxmlViewManager;
import org.unibl.etf.util.ZipArchiver;
import org.unibl.etf.util.gui.GuiUtil;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class BooksController {
    private static final String MAIL_RECEIVER_ADDRESS = "ivan.kuruzovic@student.etf.unibl.org";

    private ObservableList<Book> books;
    private List<Book> orderedBooks;
    private RedisBookService redisBookService;
    private MemberService memberService;


    @javafx.fxml.FXML
    private TableView<Book> booksTableView;
    @javafx.fxml.FXML
    private TableColumn<Book, String> titleColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> authorColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, String> languageColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, Date> publicationDateColumn;
    @javafx.fxml.FXML
    private TableColumn<Book, Void> detailsColumn;
    @javafx.fxml.FXML
    private TextField searchTextField;

    public BooksController(){
        this.orderedBooks = new ArrayList<>();
        this.redisBookService = new RedisBookService();
        this.memberService = new MemberService();
        this.books = FXCollections.observableArrayList();

        RequestSenderUtil.loadObservableList(memberService::getBooks, this.books);
    }

    @FXML
    public void initialize() {
        setupTableView();

        setupSearchMechanism();
    }

    private void setupTableView(){
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        publicationDateColumn.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));
        detailsColumn.setCellFactory(getCellFactoryButton("View details"));

        booksTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);    // So a member can select multiple rows...
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

                            viewBookDetails(FxmlPaths.BOOK_DETAILS_VIEW, book);
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

    private void setupSearchMechanism(){
        FilteredList<Book> filteredBooks = new FilteredList<>(books, b -> true);

        searchTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }

                String searchKeyWord = newValue.toLowerCase();
                if(book.getBookTitle().toLowerCase().contains(searchKeyWord)){
                    return true; // We found a match in book title in the table view based on the search bar
                }
                else {
                    return false;
                }
            });
        });

        SortedList<Book> sortedProducts = new SortedList<>(filteredBooks);
        sortedProducts.comparatorProperty().bind(booksTableView.comparatorProperty());

        booksTableView.setItems(sortedProducts);
    }

    private void viewBookDetails(String fxmlPath, Book book) {
        FxmlViewManager viewManager = new FxmlViewManager(fxmlPath, "Book details", new BookDetailsController(book));
        viewManager.showView();
    }

    @javafx.fxml.FXML
    public void orderBookAction(ActionEvent actionEvent) {
        orderedBooks = booksTableView.getSelectionModel().getSelectedItems();
        String zipFileName = "book-order_" + System.currentTimeMillis();

        Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Book order placement", "You are about to make an order of books.", "Are You sure You want to do that?", ButtonType.YES, ButtonType.NO);

        if(answer.get() == ButtonType.YES){
            try{
                String zipPath = ZipArchiver.zipBooks(orderedBooks, zipFileName);
                EmailRequestDTO emailRequestDTO = new EmailRequestDTO(orderedBooks, MAIL_RECEIVER_ADDRESS, zipPath, zipFileName + ".zip");
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                String json = gson.toJson(emailRequestDTO);
//                System.out.println("json: \n" + json);

                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(emailRequestDTO);
                //System.out.println("json: \n" + json);

                memberService.sendEmail(emailRequestDTO);
                //System.out.println("Email was sent? " + emailSent);

                //EmailSender.sendEmailWithAttachment(orderedBooks, MAIL_RECEIVER_ADDRESS, zipPath, zipFileName + ".zip");


            }
            catch (Exception e){
                GuiUtil.showAlert(Alert.AlertType.ERROR, "Book order placement", "FAILED", "An error was encountered while making your order");
                MyLogger.logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }
        }



    }
}
