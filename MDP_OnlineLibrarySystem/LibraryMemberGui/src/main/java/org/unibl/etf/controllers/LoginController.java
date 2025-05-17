package org.unibl.etf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.unibl.etf.exception.InvalidLoginException;
import org.unibl.etf.model.LibraryMember;
import org.unibl.etf.model.LibraryMemberLoginDTO;
import org.unibl.etf.service.MemberService;
import org.unibl.etf.util.*;
import org.unibl.etf.util.gui.FxmlPaths;
import org.unibl.etf.util.gui.FxmlViewManager;
import org.unibl.etf.util.gui.GuiUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class LoginController {
    private MemberService memberService;

    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private ImageView loginImageView;

    public LoginController(){
        this.memberService = new MemberService();
    }


    @FXML
    public void initialize() {
        Image image = new Image("/images/library-icon.png");
        loginImageView.setImage(image);
        loginImageView.setFitWidth(200);
        loginImageView.setFitHeight(150);
    }

    private void showWindow(Parent root, String title){
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    @FXML
    public void registerMemberAction(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(FxmlPaths.USER_REGISTER_VIEW));
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        showWindow(root, "Registration");
    }


    @FXML
    public void signInAction(ActionEvent actionEvent) {
        String username = usernameTextField.getText(), password = passwordTextField.getText();

        if(username.isBlank() || password.isBlank()){
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password!", "Try again.");

        }
        else{
            String passwordHash = TextHasher.getHash(password);
            try{
                LibraryMember member = memberService.loginMember(new LibraryMemberLoginDTO(username, passwordHash));
                if(member != null){
                    FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.LIBRARY_MEMBER_MAIN_VIEW, "Library Member", new LibraryMemberMainController(member));
                    viewManager.showView();

                    closeView(actionEvent);
                }
            } catch (InvalidLoginException e) {
                MyLogger.logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private void closeView(ActionEvent actionEvent){
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
