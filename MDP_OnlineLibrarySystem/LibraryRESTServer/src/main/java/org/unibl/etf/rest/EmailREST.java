package org.unibl.etf.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.unibl.etf.model.EmailRequestDTO;
import org.unibl.etf.model.RestMessage;
import org.unibl.etf.service.EmailService;

import javax.mail.MessagingException;

@Path("/mail")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailREST {
    private EmailService emailService;

    public EmailREST() {
        this.emailService = new EmailService();
    }


    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(EmailRequestDTO email) {

        try {
            emailService.sendEmailWithAttachment(email.getOrderedBooks(), email.getToEmail(), email.getAttachmentPath(), email.getAttachmentName());
            System.out.println("[REST]: Sent email");
            return Response.ok().build();
        } catch (MessagingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RestMessage(e.getMessage())).build();
        }
    }
}
