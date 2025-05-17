package org.unibl.etf.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.unibl.etf.util.FxmlPaths;
import org.unibl.etf.util.FxmlViewManager;

public class Main extends Application {


    public static void main(String[] args) {
//        BookService bookService = new BookService();
//        List<Book> books = bookService.getBooks();
//
//        System.out.println(books);


        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.LIBRARY_LOGIN_VIEW, "Library Login");
        viewManager.showView();
    }
}
