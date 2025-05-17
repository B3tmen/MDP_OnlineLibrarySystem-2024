package org.unibl.etf.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.scene.control.Alert;
import org.unibl.etf.exception.InvalidLoginException;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.model.user.librarian.LibrarianLoginDTO;
import org.unibl.etf.model.RestMessage;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.GuiUtil;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class LibrarianService {
    private static final int LOGIN_LIBRARIAN_NOT_FOUND = 404;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final ConfigReader configReader = ConfigReader.getInstance();

    public Librarian loginLibrarian(LibrarianLoginDTO librarianDTO) throws InvalidLoginException {
        String url = configReader.getLoginURL();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Entity<LibrarianLoginDTO> entity = Entity.entity(librarianDTO, MediaType.APPLICATION_JSON);
        Response response = target.request(MediaType.APPLICATION_JSON).post(entity);


        if(response.getStatus() == LOGIN_LIBRARIAN_NOT_FOUND){
            RestMessage msg = response.readEntity(RestMessage.class);
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Account login", "An account with Your entered credentials does not exist.", "Please check your username and password.");
            throw new InvalidLoginException(msg.getMessage());
        }
        else if(response.getStatus() == INTERNAL_SERVER_ERROR){
            RestMessage msg = response.readEntity(RestMessage.class);
            GuiUtil.showAlert(Alert.AlertType.ERROR, "Account login", "There was an error with the server.", "Please try again later.");
            throw new InvalidLoginException(msg.getMessage());
        }
        else{
            return response.readEntity(Librarian.class);
        }
    }
}
