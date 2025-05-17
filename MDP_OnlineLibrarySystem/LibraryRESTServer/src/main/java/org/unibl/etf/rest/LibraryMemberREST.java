package org.unibl.etf.rest;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.unibl.etf.exceptions.MemberAlreadyExistsException;
import org.unibl.etf.exceptions.MemberNotActivatedException;
import org.unibl.etf.exceptions.MemberNotFoundException;
import org.unibl.etf.model.Book;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.model.user.member.LibraryMemberLoginDTO;
import org.unibl.etf.model.RestMessage;
import org.unibl.etf.service.BookService;
import org.unibl.etf.service.LibraryMemberService;
import org.unibl.etf.util.MyLogger;

import java.util.List;
import java.util.logging.Level;

@Path("/users")
public class LibraryMemberREST {
    private static final int LOGIN_UNAUTHORIZED = 401;
    private static final int LOGIN_FORBIDDEN = 403;
    private static final int LOGIN_MEMBER_NOT_FOUND = 404;
    private static final int USERNAME_EXISTS_CONFLICT = 409;     //409 - conflict
    private static final int INTERNAL_SERVER_ERROR = 500;

    private LibraryMemberService memberService;
    private BookService bookService;

    public LibraryMemberREST() {
        memberService = new LibraryMemberService();
        bookService = new BookService();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello Jersey";
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(LibraryMember member) {
        try{
            boolean success = this.memberService.registerMember(member);
            if(success){
                System.out.println("[REST]: Registered a Library Member to users.xml");
                return Response.ok().build();
            }
            else{
                return Response.status(INTERNAL_SERVER_ERROR).build();
            }
        }
        catch (MemberAlreadyExistsException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            return Response.status(USERNAME_EXISTS_CONFLICT).entity(new RestMessage(e.getMessage())).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LibraryMemberLoginDTO memberLoginDTO) {
        try{
            LibraryMember success = this.memberService.checkLogin(memberLoginDTO);
            if(success != null){
                System.out.println("[REST]: Logged in a Library Member");
                return Response.ok().entity(success).build();
            }
            else{
                return Response.status(INTERNAL_SERVER_ERROR).build();
            }
        }
        catch (MemberNotFoundException e){
            MyLogger.logger.log(Level.WARNING, e.getMessage());
            return Response.status(LOGIN_MEMBER_NOT_FOUND).entity(new RestMessage(e.getMessage())).build();
        }
        catch (MemberNotActivatedException e) {
            MyLogger.logger.log(Level.WARNING, e.getMessage());
            return Response.status(LOGIN_FORBIDDEN).entity(new RestMessage(e.getMessage())).build();
        }
    }

    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks() {
        List<Book> books = bookService.getAll();

        if(books != null && !books.isEmpty()) {
            System.out.println("[REST]: Sent a list of books to a LibraryMember");
            return Response.ok(books).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
