package org.unibl.etf.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.unibl.etf.controllers.LibraryMemberMainController;
import org.unibl.etf.exception.InvalidLoginException;
import org.unibl.etf.model.*;
import org.unibl.etf.model.book.Book;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.config.ConfigReader;
import org.unibl.etf.util.gui.FxmlPaths;
import org.unibl.etf.util.gui.FxmlViewManager;
import org.unibl.etf.util.gui.GuiUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class MemberService {
    private static final int LOGIN_FORBIDDEN = 403;
    private static final int LOGIN_MEMBER_NOT_FOUND = 404;
    private static final ConfigReader configReader = ConfigReader.getInstance();


    public LibraryMember loginMember(LibraryMemberLoginDTO memberLoginDTO) throws InvalidLoginException {
        String url = configReader.getLoginURL();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Entity<LibraryMemberLoginDTO> entity = Entity.entity(memberLoginDTO, MediaType.APPLICATION_JSON);
        Response response = target.request(MediaType.APPLICATION_JSON).post(entity);

        if(response.getStatus() == LOGIN_FORBIDDEN){
            RestMessage msg = response.readEntity(RestMessage.class);
            GuiUtil.showAlert(Alert.AlertType.WARNING, "Account login", "Your account was not activated by a library worker.", "Please wait and try again later");
            throw new InvalidLoginException(msg.getMessage());
        }
        else if(response.getStatus() == LOGIN_MEMBER_NOT_FOUND){
            RestMessage msg = response.readEntity(RestMessage.class);
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Account login", "An account with Your entered credentials does not exist.", "Please check your username and password.");
            throw new InvalidLoginException(msg.getMessage());
        }


        return response.readEntity(LibraryMember.class);

    }

    public void registerMember(LibraryMember member) throws InvalidLoginException {
        String url = configReader.getRegisterURL();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Perform the long-running request
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(member, MediaType.APPLICATION_JSON));

                if(response.getStatusInfo()== Response.Status.OK)
                    return true;
                else if(response.getStatusInfo() == Response.Status.INTERNAL_SERVER_ERROR)
                    return false;
                else
                {
                    RestMessage message = response.readEntity(RestMessage.class);
                    throw new InvalidLoginException(message.getMessage());
                }
            }
        };

        task.setOnSucceeded(event -> {
            boolean validRegistration = task.getValue();

            if (validRegistration) {
                GuiUtil.showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "You have been successfully registered on the system", "Try logging in now");
            }
            else{
                GuiUtil.showAlert(Alert.AlertType.ERROR, "ERROR", "A server error was encountered", "Please check if Your entered Data is correct");
            }
        });

        new Thread(task).start();
    }

    public void sendEmail(EmailRequestDTO emailRequestDTO) throws InvalidLoginException {
        String url = configReader.getEmailURL();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Perform the long-running request
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(emailRequestDTO, MediaType.APPLICATION_JSON));

                if(response.getStatusInfo() == Response.Status.OK)
                    return true;
                else if(response.getStatusInfo() == Response.Status.INTERNAL_SERVER_ERROR)
                    return false;
                else
                {
                    RestMessage message = response.readEntity(RestMessage.class);
                    if(message.getMessage().equals(Response.Status.OK.toString()))
                        return true;
                    else {
                        return false;
                    }
                }
            }
        };

        task.setOnSucceeded(event -> {
            boolean emailWasSent = task.getValue();

            if(emailWasSent){
                GuiUtil.showAlert(Alert.AlertType.INFORMATION, "Book order placement", "SUCCESS", "Your order was succesfully placed");
            }
            else{
                GuiUtil.showAlert(Alert.AlertType.ERROR, "Book order placement", "ERROR", "There was an error processing Your order");
            }
        });

        new Thread(task).start();
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        String url = configReader.getBooksURL();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Response response = target.request().get();

        if(response.getStatusInfo() == Response.Status.OK) {
            String json = response.readEntity(String.class);

            // Use Jackson to deserialize the JSON into a List<Book>
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                books = objectMapper.readValue(json, new TypeReference<List<Book>>() {});
            } catch (IOException e) {
                MyLogger.logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }

        }

        for(Book book : books){
            book.readContentFromFile();
        }

        return books;
    }
}
