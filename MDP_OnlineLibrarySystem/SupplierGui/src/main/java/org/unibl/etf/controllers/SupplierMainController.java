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
import org.unibl.etf.client.ProtocolMessages;
import org.unibl.etf.client.SupplierClient;
import org.unibl.etf.rabbitmq.RabbitMQMessageReceiver;
import org.unibl.etf.util.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class SupplierMainController {
    private Socket socket;
    private SupplierClient supplierClient;

    private PendingBookOrdersController pendingBookOrdersController;
    private AllBooksController allBooksController;
    private boolean isSideBarHidden = true;

    @javafx.fxml.FXML
    private Button sideBarButton;
    @javafx.fxml.FXML
    private FontIcon sidebarButtonIcon;
    @javafx.fxml.FXML
    private Label librarianUsernameLabel;
    @javafx.fxml.FXML
    private AnchorPane mainContentAnchorPane;
    @javafx.fxml.FXML
    private VBox sideBarContainerVBox;

    public SupplierMainController(){
        this.allBooksController = new AllBooksController();

        int port = ConfigReader.getInstance().getSupplierServerPort();
        try {
            this.socket = new Socket("localhost", port);
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        connectToServer();

        this.pendingBookOrdersController = new PendingBookOrdersController(supplierClient);

        RabbitMQMessageReceiver receiver = new RabbitMQMessageReceiver(pendingBookOrdersController);
        receiver.start();

    }

    @FXML
    public void initialize() {
        initSideBar();
    }

    private void connectToServer() {
        this.supplierClient = new SupplierClient(this.socket, this.allBooksController);
        this.supplierClient.listenToMessages();

        this.supplierClient.sendMsgToServer(ProtocolMessages.GET_SUPPLIER_BOOKS_REQUEST);
    }


    private void initSideBar(){
        double width = sideBarContainerVBox.getWidth();
        sideBarContainerVBox.setTranslateX(-150);   // TODO: change to width property of sidebar
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
            if(fxmlViewPath.equals(FxmlPaths.PENDING_BOOK_ORDERS_VIEW)){
                loader.setController(pendingBookOrdersController);
            }
            else if(fxmlViewPath.equals(FxmlPaths.ALL_BOOKS_VIEW)){
                loader.setController(allBooksController);
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

    @javafx.fxml.FXML
    public void showPendingOrdersAction(ActionEvent actionEvent) {
        setMainContentAnchorPane(FxmlPaths.PENDING_BOOK_ORDERS_VIEW);
    }

    @javafx.fxml.FXML
    public void showAllBooksAction(ActionEvent actionEvent) {
        setMainContentAnchorPane(FxmlPaths.ALL_BOOKS_VIEW);
    }

    @javafx.fxml.FXML
    public void logOutAction(ActionEvent actionEvent) {
        Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Log out", "Are you sure you want to log out?", "", ButtonType.YES, ButtonType.NO);
        if(answer.get() == ButtonType.YES){
            Scene scene = ((Button) actionEvent.getSource()).getScene();
            Stage stage = (Stage) scene.getWindow();

            closeConnectionsForLogout();

            FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.SUPPLIER_MAIN_VIEW, "Library Main");
            viewManager.showView();

            stage.close();
        }
    }

    private void closeConnectionsForLogout() {
        supplierClient.closeFromController();
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


}
