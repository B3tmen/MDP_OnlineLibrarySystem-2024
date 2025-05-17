package org.unibl.etf.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.unibl.etf.exception.InvalidLoginException;
import org.unibl.etf.model.LibraryMember;
import org.unibl.etf.service.MemberService;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.TextHasher;
import org.unibl.etf.util.gui.GuiUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class RegisterController {
    private MemberService memberService;

    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Button showPasswordConfirmButton;
    @FXML
    private Button showPasswordRegularButton;
    @FXML
    private Button unshowPasswordConfirmButton;
    @FXML
    private Button unshowPasswordRegularButton;
    @FXML
    private PasswordField passwordRegularPasswordField;
    @FXML
    private PasswordField passwordConfirmPasswordField;
    @FXML
    private TextField passwordConfirmRevealedTextField;
    @FXML
    private TextField passwordRegularRevealedTextField;

    public RegisterController() {
        this.memberService = new MemberService();
    }

    @FXML
    public void initialize() {
        setupPasswordTextFields();
        setupSeePasswordButtons();
    }

    private void setupSeePasswordButtons(){
        double x1 = showPasswordRegularButton.getLayoutX(), y1 = showPasswordRegularButton.getLayoutY();
        unshowPasswordRegularButton.setLayoutX(x1);
        unshowPasswordRegularButton.setLayoutY(y1);
        unshowPasswordRegularButton.setVisible(false);

        double x2 = showPasswordConfirmButton.getLayoutX(), y2 = showPasswordConfirmButton.getLayoutY();
        unshowPasswordConfirmButton.setLayoutX(x2);
        unshowPasswordConfirmButton.setLayoutY(y2);
        unshowPasswordConfirmButton.setVisible(false);
    }

    private void setupPasswordTextFields(){
        double x1 = passwordRegularPasswordField.getLayoutX(), y1 = passwordRegularPasswordField.getLayoutY();
        passwordRegularRevealedTextField.setLayoutX(x1);
        passwordRegularRevealedTextField.setLayoutY(y1);
        passwordRegularRevealedTextField.setVisible(false);
        passwordRegularRevealedTextField.textProperty().bindBidirectional(passwordRegularPasswordField.textProperty());   // synchronizing the text content

        double x2 = passwordConfirmPasswordField.getLayoutX(), y2 = passwordConfirmPasswordField.getLayoutY();
        passwordConfirmRevealedTextField.setLayoutX(x2);
        passwordConfirmRevealedTextField.setLayoutY(y2);
        passwordConfirmRevealedTextField.setVisible(false);
        passwordConfirmRevealedTextField.textProperty().bindBidirectional(passwordConfirmPasswordField.textProperty());   // synchronizing the text content
    }

    @FXML
    public void registerAction(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String address = addressTextField.getText();
        String password = passwordRegularPasswordField.getText();
        String confirmPassword = passwordConfirmPasswordField.getText();
        boolean isValid = checkFields(username, firstName, lastName, email, phoneNumber, address, password, confirmPassword);

        if (isValid) {
            String passwordHash = TextHasher.getHash(password);
            LibraryMember member = new LibraryMember(username, passwordHash, firstName, lastName, address, email);
            try {
                memberService.registerMember(member);
            } catch (InvalidLoginException e) {
                MyLogger.logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private boolean checkFields(String username, String firstName, String lastName, String email, String phoneNumber, String address, String password, String confirmPassword) {
        boolean isValid = true;

        if(username.isBlank() || firstName.isBlank() || lastName.isBlank() || email.isBlank() || phoneNumber.isBlank() || address.isBlank() || password.isBlank() || confirmPassword.isBlank()){
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Error", "Fields are empty", "Please enter data in all the fields");
            isValid = false;
        }

        if(!password.equals(confirmPassword)){
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Error", "Passwords don't match!", "Please make sure the entered passwords are identical");
            isValid = false;
        }


        return isValid;
    }

    @FXML
    public void showRegularPassword(ActionEvent actionEvent) {
        showPasswordRegularButton.setVisible(false);
        unshowPasswordRegularButton.setVisible(true);

        passwordRegularPasswordField.setVisible(false);
        passwordRegularRevealedTextField.setVisible(true);
    }

    @FXML
    public void unshowRegularPassword(ActionEvent actionEvent) {
        showPasswordRegularButton.setVisible(true);
        unshowPasswordRegularButton.setVisible(false);

        passwordRegularPasswordField.setVisible(true);
        passwordRegularRevealedTextField.setVisible(false);
    }

    @FXML
    public void showConfirmPassword(ActionEvent actionEvent) {
        showPasswordConfirmButton.setVisible(false);
        unshowPasswordConfirmButton.setVisible(true);

        passwordConfirmPasswordField.setVisible(false);
        passwordConfirmRevealedTextField.setVisible(true);
    }

    @FXML
    public void unshowConfirmPassword(ActionEvent actionEvent) {
        showPasswordConfirmButton.setVisible(true);
        unshowPasswordConfirmButton.setVisible(false);

        passwordConfirmPasswordField.setVisible(true);
        passwordConfirmRevealedTextField.setVisible(false);
    }
}
