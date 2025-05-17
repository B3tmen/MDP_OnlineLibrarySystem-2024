package org.unibl.etf.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LibraryMemberService {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private static final String BASE_URL = configReader.getMembersURL();

    public List<LibraryMember> getMembers() {
        List<LibraryMember> members = new ArrayList<>();
        String url = BASE_URL;

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Response response = target.request().get();

        if(response.getStatusInfo() == Response.Status.OK) {
            String json = response.readEntity(String.class);

            // Use Jackson to deserialize the JSON into a List<LibUser>
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                members = objectMapper.readValue(json, new TypeReference<List<LibraryMember>>() {});

            } catch (IOException e) {
                MyLogger.logger.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }
        }

        return members;
    }

    public boolean acceptRegistrationMember(LibraryMember member) {
        String url = BASE_URL + "/accept/" + member.getUsername();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        //Entity<LibraryMember> entity = Entity.entity(member, MediaType.APPLICATION_JSON);
        Response response = target.request().put(Entity.text(""));

        if(response.getStatusInfo() == Response.Status.OK) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean rejectRegistrationMember(LibraryMember member) {
        String url = BASE_URL + "/reject/" + member.getUsername();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        //Entity<LibraryMember> entity = Entity.entity(member, MediaType.APPLICATION_JSON);
        Response response = target.request().put(Entity.text(""));

        if(response.getStatusInfo() == Response.Status.OK) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean deleteMember(LibraryMember member) {
        String url = BASE_URL + "/delete";

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);      // we have to do this because otherwise we can't send a specified entity (the Member) for deletion...
                                                                                                        // without it, it would be target.request.delete() WITHOUT the entity as the parameter...
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target(url);
        Entity<LibraryMember> memberEntity = Entity.entity(member, MediaType.APPLICATION_JSON);
        Response response = target.request().build("DELETE", memberEntity).invoke();            // REMINDER: It WILL set off a WARNING log message in console, but it will still go through with the request

        if(response.getStatusInfo() == Response.Status.NO_CONTENT) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean blockMember(LibraryMember member) {
        String url = BASE_URL + "/block/" + member.getUsername();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        //Entity<LibraryMember> entity = Entity.entity(member, MediaType.APPLICATION_JSON);
        Response response = target.request().put(Entity.text(""));

        if(response.getStatusInfo() == Response.Status.OK) {
            return true;
        }
        else {
            return false;
        }
    }

}
