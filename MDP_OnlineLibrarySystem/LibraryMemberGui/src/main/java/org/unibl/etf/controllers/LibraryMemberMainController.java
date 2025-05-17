package org.unibl.etf.controllers;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.unibl.etf.model.book.Book;
import org.unibl.etf.model.LibraryMember;
import org.unibl.etf.multicast.MulticastServer;
import org.unibl.etf.redis.RedisBookService;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.gui.FxmlPaths;
import org.unibl.etf.util.gui.FxmlViewManager;
import org.unibl.etf.util.gui.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class LibraryMemberMainController {
    private LibraryMember currentUser;
    private RedisBookService redisBookService;
    private boolean isSideBarHidden = true;

    private AllBookSuggestionsController bookSuggestionsController;
    private ChatMainController chatMainController;
    private MulticastServer multicastServer;


    @javafx.fxml.FXML
    private Button sideBarButton;
    @javafx.fxml.FXML
    private FontIcon sidebarButtonIcon;
    @javafx.fxml.FXML
    private Button logOutButton;
    @javafx.fxml.FXML
    private Button accountButton;
    @javafx.fxml.FXML
    private Label customerUsernameLabel;
    @javafx.fxml.FXML
    private VBox sideBarContainerVBox;
    @javafx.fxml.FXML
    private AnchorPane mainContentAnchorPane;

    public LibraryMemberMainController(LibraryMember currentUser){
        this.currentUser = currentUser;
        this.redisBookService = new RedisBookService();
        this.bookSuggestionsController = new AllBookSuggestionsController();
        this.chatMainController = new ChatMainController(currentUser);

        //addTestBooks();
        multicastServer = new MulticastServer(bookSuggestionsController);
    }

    @FXML
    public void initialize() {

        initSideBar();
        this.customerUsernameLabel.setText(currentUser.getUsername());
    }

    private void addTestBooks(){
        redisBookService.saveBook(new Book("t1", "a1", new Date(), "l1", "fp1", "c1"));
        redisBookService.saveBook(new Book("t2", "a2", new Date(), "l2", "fp2", "c2"));
        redisBookService.saveBook(new Book("t3", "a3", new Date(), "l3", "fp3", "c3"));
    }

    private void initSideBar(){
        double width = sideBarContainerVBox.getWidth();
        sideBarContainerVBox.setTranslateX(-150);   // TODO: change to width property of sidebar
    }

    @javafx.fxml.FXML
    public void hideSideBar(Event event) {
        double sideBarWidth = sideBarContainerVBox.getWidth();
        List<TranslateTransition> transitions = new ArrayList<>();  //0=sideBar, 1=title, 2=productContainer
        transitions.add(new TranslateTransition(Duration.millis(400), sideBarContainerVBox));
        transitions.add(new TranslateTransition(Duration.millis(400), mainContentAnchorPane));


        TranslateTransition slideTransition = new TranslateTransition();
        slideTransition.setDuration(Duration.seconds(0.4));
        slideTransition.setNode(sideBarContainerVBox);
        if(isSideBarHidden){    // opening the sidebar
            for(int i = 0; i < transitions.size(); i++){
                TranslateTransition transition = transitions.get(i);
                if(i == 0){
                    transition.setToX(0);
                    sideBarContainerVBox.setTranslateX(-sideBarWidth);
                    animateSideBarIcon(false);

                    transition.setOnFinished((ActionEvent e)-> {
                        isSideBarHidden = false;
                    });
                }
                else if(i == 1){
                    // when the sidebar shows, the main content should contract accordingly

                    animateAnchor(new SimpleDoubleProperty(50), 10, mainContentAnchorPane);
                }

            }


        }
        else{                   // closing the sidebar
            for(int i = 0; i < transitions.size(); i++){
                TranslateTransition transition = transitions.get(i);
                if(i == 0){
                    transition.setToX(-sideBarWidth);
                    sideBarContainerVBox.setTranslateX(0);
                    animateSideBarIcon(true);

                    transition.setOnFinished((ActionEvent e)-> {
                        isSideBarHidden = true;
                    });
                }
                else if(i == 1){
                    // when the sidebar shows, the main content should contract accordingly

                    animateAnchor(new SimpleDoubleProperty(50), -sideBarWidth + 10, mainContentAnchorPane);
                }
            }

        }

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(transitions);
        parallelTransition.play();
    }

    private void animateAnchor(DoubleProperty leftAnchor, double value, Node container) {
        Timeline timeline = new Timeline();
        KeyValue keyValue;

        keyValue = new KeyValue(leftAnchor, value); // Stretch to the left

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.4), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        leftAnchor.addListener((obs, oldVal, newVal) -> AnchorPane.setLeftAnchor(container, newVal.doubleValue()));
    }

    private void animateSideBarIcon(boolean isClosingAction){
        if(isClosingAction){
            sidebarButtonIcon.setIconLiteral("fas-bars");
        }
        else{
            sidebarButtonIcon.setIconLiteral("fas-window-close");
        }
    }

    private void setMainContentAnchorPane(String fxmlViewPath){
        //mainContentAnchorPane.getChildren().removeAll();
        Parent content = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlViewPath));

        try {
            if(fxmlViewPath.equals(FxmlPaths.CHAT_MAIN_VIEW)){
                loader.setController(chatMainController);
            }
            else if(fxmlViewPath.equals(FxmlPaths.ALL_BOOK_SUGGESTIONS_VIEW)){
                loader.setController(bookSuggestionsController);
            }

            content = loader.load();
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        mainContentAnchorPane.getChildren().setAll(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);

        //setSubContentCssStyle(content, CssPaths.CUSTOMER_ACCOUNT_CSS);
    }

    private void closeEverything(){
       multicastServer.interrupt();
    }

    @javafx.fxml.FXML
    public void showBooksAction(ActionEvent actionEvent) {
        setMainContentAnchorPane(FxmlPaths.BOOKS_VIEW);
    }

    @javafx.fxml.FXML
    public void showChatSectionAction(ActionEvent actionEvent) {
        setMainContentAnchorPane(FxmlPaths.CHAT_MAIN_VIEW);
    }

    @javafx.fxml.FXML
    public void logOutAction(ActionEvent actionEvent) {
        Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Log out", "Are you sure you want to log out?", "", ButtonType.YES, ButtonType.NO);
        if(answer.get() == ButtonType.YES){
            Scene scene = ((Button) actionEvent.getSource()).getScene();
            Stage stage = (Stage) scene.getWindow();

            closeConnectionsForLogout();

            FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.USER_LOGIN_VIEW, "Library Member");
            viewManager.showView();

            stage.close();
        }
    }

    @javafx.fxml.FXML
    public void showAllBookRequestsAction(ActionEvent actionEvent) {
        setMainContentAnchorPane(FxmlPaths.ALL_BOOK_SUGGESTIONS_VIEW);
    }

    @javafx.fxml.FXML
    public void showBookWriteSuggestionAction(ActionEvent actionEvent) {
        setMainContentAnchorPane(FxmlPaths.WRITE_BOOK_SUGGESTION_VIEW);
    }

    public void closeConnectionsForLogout(){
        multicastServer.setRunning(false);
        chatMainController.closeEverything();
    }
}
