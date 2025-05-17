package org.unibl.etf.util.gui;

public class FxmlPaths {

    private static final String c = "/";  // used for getClass().getResource("..../....")

    //general folder paths
    private static final String VIEW_FOLDER =  c + "view" + c;

    public static final String USER_LOGIN_VIEW = VIEW_FOLDER + "LoginView.fxml";
    public static final String USER_REGISTER_VIEW = VIEW_FOLDER + "RegisterView.fxml";

    public static final String LIBRARY_MEMBER_MAIN_VIEW = VIEW_FOLDER + "LibraryMemberMainView.fxml";
    public static final String CHAT_MAIN_VIEW = VIEW_FOLDER + "ChatMainView.fxml";
    public static final String CHAT_WITH_MEMBER_VIEW = VIEW_FOLDER + "ChatWithMemberView.fxml";

    public static final String BOOKS_VIEW = VIEW_FOLDER + "BooksView.fxml";
    public static final String BOOK_DETAILS_VIEW = VIEW_FOLDER + "BookDetailsView.fxml";
    public static final String WRITE_BOOK_SUGGESTION_VIEW = VIEW_FOLDER + "WriteBookSuggestionView.fxml";
    public static final String ALL_BOOK_SUGGESTIONS_VIEW = VIEW_FOLDER + "AllBookSuggestionsView.fxml";
}
