package org.unibl.etf.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.unibl.etf.model.Book;
import org.unibl.etf.service.BookService;

import java.util.List;

@Path("/library/books")
public class LibraryMainBookREST {
    private BookService bookService;

    public LibraryMainBookREST() {
        this.bookService = new BookService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks() {
        List<Book> books = bookService.getAll();

        if(books != null && !books.isEmpty()) {
            System.out.println("[REST]: Sent books to library");
            return Response.ok(books).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBook(Book book) {
        int insertedBooks = bookService.insert(book);
        if(insertedBooks > 0){
            System.out.println("[REST]: Added books to storage");
            return Response.status(Response.Status.CREATED).build();
        }
        else{
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(Book book) {
        int updatedBooks = bookService.update(book);
        if(updatedBooks > 0){
            System.out.println("[REST]: Updated a book");
            return Response.ok().entity(book).build();
        }
        else{
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteBook(Book book) {
        int deletedBooks = bookService.delete(book);
        if(deletedBooks > 0){
            System.out.println("[REST]: Deleted a book");
            return Response.status(Response.Status.NO_CONTENT).build();     // status can be 200-OK, but it has to have the entity in the body of the response
        }
        else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
