package org.unibl.etf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.unibl.etf.exception.InvalidLoginException;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.model.user.librarian.LibrarianLoginDTO;
import org.unibl.etf.service.LibrarianService;
import org.unibl.etf.util.*;

import java.util.logging.Level;

public class LibraryLoginController {
    private LibrarianService librarianService;

    @javafx.fxml.FXML
    private PasswordField passwordTextField;
    @javafx.fxml.FXML
    private ImageView loginImageView;
    @javafx.fxml.FXML
    private TextField usernameTextField;

    public LibraryLoginController(){
        this.librarianService = new LibrarianService();
    }

    @FXML
    private void initialize(){
        Image image = new Image("/images/library-icon.png");
        loginImageView.setImage(image);
        loginImageView.setFitWidth(200);
        loginImageView.setFitHeight(150);
    }


    @javafx.fxml.FXML
    public void signInAction(ActionEvent actionEvent) {
        String username = usernameTextField.getText(), password = passwordTextField.getText();

        if(username.isBlank() || password.isBlank()){
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password!", "Try again.");
        }
        else{
            String passwordHash = TextHasher.getHash(password);
            try{
                Librarian librarian = librarianService.loginLibrarian(new LibrarianLoginDTO(username, passwordHash));
                if(librarian != null){
                    FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.LIBRARY_MAIN_VIEW, "Library Main", new LibraryMainController(librarian));
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
