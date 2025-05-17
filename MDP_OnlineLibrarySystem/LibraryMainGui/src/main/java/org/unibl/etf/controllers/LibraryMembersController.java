package org.unibl.etf.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.service.LibraryMemberService;
import org.unibl.etf.service.LoadUtils;
import org.unibl.etf.util.GuiUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LibraryMembersController {
    private LibraryMemberService memberService;
    private ObservableList<LibraryMember> members;

    @javafx.fxml.FXML
    private TableView<LibraryMember> membersTableView;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, String> pendingRequestColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, String> usernameColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, String> firstNameColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, String> lastNameColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, String> addressColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, String> emailColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, Boolean> activeColumn;
    @javafx.fxml.FXML
    private TableColumn<LibraryMember, Boolean> blockedColumn;


    public LibraryMembersController() {
        this.memberService = new LibraryMemberService();
        this.members = FXCollections.observableArrayList();

        LoadUtils.loadObservableListFromDatabase(memberService::getMembers, members);
    }

    @FXML
    public void initialize() {
        setupTableView();
    }


    private void setupTableView() {
        pendingRequestColumn.setCellValueFactory(param -> {
            boolean isActive = param.getValue().isActiveAccount();
            if(!isActive){
                return new SimpleStringProperty("PENDING");
            }
            else{
                return new SimpleStringProperty("-");
            }
        });

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("activeAccount"));
        blockedColumn.setCellValueFactory(new PropertyValueFactory<>("blocked"));

        membersTableView.setItems(members);
    }

    private void refreshTable(){
        this.membersTableView.refresh();
    }



    @javafx.fxml.FXML
    public void acceptRegistrationAction(ActionEvent actionEvent) {
        LibraryMember selectedMember = membersTableView.getSelectionModel().getSelectedItem();

        if(selectedMember != null){

            Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Registration", "You are about to accept a registration request of a member", "Are you sure you want to do this?", ButtonType.YES, ButtonType.NO);
            if(answer.get() == ButtonType.YES){
                boolean accepted = memberService.acceptRegistrationMember(selectedMember);
                if(accepted){
                    selectedMember.setActiveAccount(true);
                    GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Registration", "You have successfully registered a library member", "Press OK to continue.");

                    refreshTable();
                }

            }
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Member Registration", "Please select a member for accepting a registration", "Try again.");
        }
    }

    @javafx.fxml.FXML
    public void rejectRegistrationAction(ActionEvent actionEvent) {
        LibraryMember selectedMember = membersTableView.getSelectionModel().getSelectedItem();

        if(selectedMember != null){

            Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Registration", "You are about to reject a registration request of a member", "Are you sure you want to do this?", ButtonType.YES, ButtonType.NO);
            if(answer.get() == ButtonType.YES){
                boolean rejected = memberService.rejectRegistrationMember(selectedMember);
                if(rejected){
                    selectedMember.setActiveAccount(false);
                    GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Registration", "You have successfully rejected a library member", "Press OK to continue.");

                    refreshTable();
                }


            }
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Member Registration", "Please select a member for rejecting a registration", "Try again.");
        }
    }

    @javafx.fxml.FXML
    public void blockMemberAction(ActionEvent actionEvent) {
        LibraryMember selectedMember = membersTableView.getSelectionModel().getSelectedItem();

        if(selectedMember != null){

            Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Blockage", "You are about to block a library member", "Are you sure you want to do this?", ButtonType.YES, ButtonType.NO);
            if(answer.get() == ButtonType.YES){
                boolean blocked = memberService.blockMember(selectedMember);
                if(blocked){
                    selectedMember.setBlocked(true);
                    GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Registration", "You have successfully blocked a library member", "Press OK to continue.");

                    refreshTable();
                }

            }
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Member Blockage", "Please select a member for blocking them", "Try again.");
        }
    }

    @javafx.fxml.FXML
    public void deleteMemberAction(ActionEvent actionEvent) {
        LibraryMember selectedMember = membersTableView.getSelectionModel().getSelectedItem();

        if(selectedMember != null){

            Optional<ButtonType> answer = GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Blockage", "You are about to block a library member", "Are you sure you want to do this?", ButtonType.YES, ButtonType.NO);
            if(answer.get() == ButtonType.YES){
                boolean deleted = memberService.deleteMember(selectedMember);
                if(deleted){
                    members.remove(selectedMember);
                    GuiUtil.showAlert(Alert.AlertType.CONFIRMATION, "Member Registration", "You have successfully blocked a library member", "Press OK to continue.");

                    refreshTable();
                }

            }
        }
        else{
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Member Blockage", "Please select a member for blocking them", "Try again.");
        }

    }



}
