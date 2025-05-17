package org.unibl.etf.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.unibl.etf.dao.LibraryMemberDAOImpl;
import org.unibl.etf.model.Book;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.service.BookService;
import org.unibl.etf.service.LibraryMemberService;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.XMLSerializer;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        //usersTest();

        bookTest();
    }

    private static void bookTest() throws Exception{
        BookService bookService = new BookService();
        List<Book> books = bookService.getAll();
//        bookService.delete(books.get(0));
//        bookService.delete(books.get(1));
//        bookService.delete(books.get(2));
        books.forEach(System.out::println);
        for(int i = 0; i < 3; i++){
            Book book = new Book("t" + i, "a" + i, new Date(), "l" + i, "fp" + i, "c" + i);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(book);

            bookService.insert(book);
        }
        books = bookService.getAll();
        System.out.println();
        books.forEach(System.out::println);

        //Book book = new Book("t" + 9, "a" + 67, new Date(), "l" + 0, "fp" + 0, "c" + 12345);
        //int updated = bookService.update(book);
        //System.out.println("Updated: " + updated);


       //List<Book> books = bookService.getAll();
        //System.out.println("BEFORE:");
        //books.forEach(System.out::println);
//
//        Book bookUpdated = books.get(0);
//        bookUpdated.setBookTitle("tUpdated2");
//        bookService.update(bookUpdated);
//        System.out.println();
//        System.out.println("AFTER:");
//        books.forEach(System.out::println);
//
//        int deleted = bookService.delete(bookUpdated);
//        if(deleted > 0){
//            System.out.println();
//            System.out.println("DELETION:");
//            books.forEach(System.out::println);
//        }
    }

    private static void usersTest() throws Exception {
        ConfigReader config = ConfigReader.getInstance();
        Path resourcePath = Paths.get(config.getUserPath());

        //TEST
        LibraryUser librarian1 = new Librarian("lib1", "sifra");
        LibraryUser member1 = new LibraryMember("username1", "aa", "ivan", "kuruzovic", "adress1", "mail1.com");
        LibraryUser member2 = new LibraryMember("username2", "bb", "ivan", "kuruzovic", "adress2", "mail2.com");

        // Example: Reading the content of the resource file
        try (InputStream fileInputStream = Files.newInputStream(resourcePath)) {
            // Do something with the fileInputStream
            System.out.println("File found at: " + resourcePath.toAbsolutePath());
        }

        // Serialize to XML
        List<LibraryUser> users = new ArrayList<>();
        users.add(member1);
        users.add(member2);
        users.add(librarian1);

        boolean serializeSuccess = XMLSerializer.serializeObjectList(users, resourcePath.toString());
        if (serializeSuccess) {
            System.out.println("Serialization successful.");
        } else {
            System.out.println("Serialization failed.");
        }

        // Deserialize from XML
        LibraryMemberDAOImpl dao = new LibraryMemberDAOImpl();
        List<LibraryUser> usersDeser = dao.getAll();
        LibraryMemberService memberService = new LibraryMemberService();
        //LibraryMember member = memberService.checkLogin(new LibraryMemberLoginDTO("username3", "aa")); //XMLSerializer.deserializeXML(resourcePath.toString());
        System.out.println("Users are: " + usersDeser);


        if (usersDeser != null) {
            for (LibraryUser user : usersDeser) {
                if(user instanceof LibraryMember member){
                    System.out.println("Username: " + member.getUsername());
                    System.out.println("First Name: " + member.getFirstName());
                    System.out.println("Last Name: " + member.getLastName());
                }
                else if(user instanceof Librarian librarian){
                    System.out.println("Librarian: " + librarian);
                }
            }
        }
        else {
            System.out.println("Deserialization failed.");
        }

    }
}
