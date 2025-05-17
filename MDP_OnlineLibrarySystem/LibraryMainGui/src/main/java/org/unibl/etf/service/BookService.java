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
import org.unibl.etf.model.book.Book;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BookService {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private static final String BASE_URL = configReader.getBooksURL();

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        String url = BASE_URL;

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

        return books;
    }

    public boolean addBook(Book book) {
        String url = BASE_URL + "/add";

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Entity<Book> entity = Entity.entity(book, MediaType.APPLICATION_JSON);
        Response response = target.request(MediaType.APPLICATION_JSON).post(entity);

        if(response.getStatusInfo() == Response.Status.CREATED) {
            System.out.println("Added book");
            return true;
        }
        else {
            System.out.println("Not added book");
            return false;
        }
    }

    public Book updateBook(Book book) {
        String url = BASE_URL + "/update";

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Entity<Book> entity = Entity.entity(book, MediaType.APPLICATION_JSON);
        Response response = target.request(MediaType.APPLICATION_JSON).put(entity);

        if(response.getStatusInfo() == Response.Status.OK) {
            Book updatedBook = response.readEntity(Book.class);
            return updatedBook;
        }
        else {
            return null;
        }
    }

    public boolean deleteBook(Book book) {
        String url = BASE_URL + "/delete";

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);      // we have to do this because otherwise we can't send a specified entity (the Book) for deletion...
                                                                                                       // without it, it would be target.request.delete() WITHOUT the entity...
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target(url);
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON);
        Response response = target.request().build("DELETE", bookEntity).invoke();

        if(response.getStatusInfo() == Response.Status.NO_CONTENT) {
            return true;
        }
        else {
            return false;
        }
    }
}
