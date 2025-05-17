package org.unibl.etf.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.unibl.etf.exceptions.MemberNotFoundException;
import org.unibl.etf.model.*;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.model.user.librarian.LibrarianLoginDTO;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.service.LibrarianService;
import org.unibl.etf.service.LibraryMemberService;
import org.unibl.etf.util.MyLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


@Path("/library/users")
public class LibraryMainMembersREST {
    private static final int LOGIN_MEMBER_NOT_FOUND = 404;
    private static final int LOGIN_LIBRARIAN_NOT_FOUND = 404;
    private static final int USERNAME_EXISTS_CONFLICT = 409;     //409 - conflict
    private static final int INTERNAL_SERVER_ERROR = 500;

    private LibraryMemberService libraryMemberService;
    private LibrarianService librarianService;

    public LibraryMainMembersREST() {
        this.librarianService = new LibrarianService();
        this.libraryMemberService = new LibraryMemberService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers() {
        List<LibraryUser> users = libraryMemberService.getAll();
        if(users != null && !users.isEmpty()) {
            List<LibraryMember> members = users.stream().filter(user -> user instanceof LibraryMember).map(user -> (LibraryMember) user).collect(Collectors.toList());

            System.out.println("[REST]: Sent a list of Library Members");
            return Response.ok().entity(members).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    @POST
    @Path("/librarian/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginLibrarian(LibrarianLoginDTO librarianLoginDTO){
        try{
            Librarian success = this.librarianService.checkLogin(librarianLoginDTO);
            if(success != null){
                System.out.println("[REST]: Logged in a librarian");
                return Response.ok().entity(success).build();
            }
            else {
                return Response.status(INTERNAL_SERVER_ERROR).build();
            }
        }
        catch (MemberNotFoundException e){
            MyLogger.logger.log(Level.WARNING, e.getMessage());
            return Response.status(LOGIN_LIBRARIAN_NOT_FOUND).entity(new RestMessage(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/accept/{username}")
    public Response acceptMember(@PathParam("username") String username) {
        boolean accepted = libraryMemberService.acceptRegistration(username);

        if(accepted) {
            System.out.println("[REST]: Accepted registration of a Library Member");
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/reject/{username}")
    public Response rejectMember(@PathParam("username") String username) {
        boolean rejected = libraryMemberService.rejectRegistration(username);

        if(rejected) {
            System.out.println("[REST]: Rejected registration of a Library Member");
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteMember(LibraryMember member) {
        int deleted = libraryMemberService.deleteMember(member);

        if(deleted > 0) {
            System.out.println("[REST]: Deleted a Library Member");
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/block/{username}")
    public Response blockMember(@PathParam("username") String username) {
        boolean blocked = libraryMemberService.blockMember(username);

        if(blocked) {
            System.out.println("[REST]: Blocked a Library Member");
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
