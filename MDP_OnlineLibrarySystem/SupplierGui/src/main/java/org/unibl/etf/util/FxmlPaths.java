package org.unibl.etf.util;

public class FxmlPaths {
    private static final String c = "/";  // used for getClass().getResource("..../....")

    //general folder paths
    private static final String VIEW_FOLDER =  c + "view" + c;

    public static final String SUPPLIER_MAIN_VIEW = VIEW_FOLDER + "SupplierMainView.fxml";
    public static final String ALL_BOOKS_VIEW = VIEW_FOLDER + "AllBooksView.fxml";
    public static final String PENDING_BOOK_ORDERS_VIEW = VIEW_FOLDER + "PendingBookOrdersView.fxml";
    public static final String BOOK_CONTENT_VIEW = VIEW_FOLDER + "BookContentView.fxml";
}
