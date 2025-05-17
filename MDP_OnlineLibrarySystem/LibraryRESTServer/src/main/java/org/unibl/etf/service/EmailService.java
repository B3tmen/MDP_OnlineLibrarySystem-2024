package org.unibl.etf.service;

import org.unibl.etf.model.Book;
import org.unibl.etf.util.MailConfigReader;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class  EmailService {
    private static final MailConfigReader mailConfigReader = MailConfigReader.getInstance();

    public EmailService(){

    }

    public void sendEmailWithAttachment(List<Book> orderedBooks, String toEmail, String attachmentPath, String attachmentName) throws MessagingException {
        String username = mailConfigReader.getUsername();   // don't forget to put your appropriate email and password !!!
        String password = mailConfigReader.getPassword();
        Properties props = mailConfigReader.getProperties();

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });


        String subject = "Subject: OnlineLibrary sent a ZIP File";
        String bodyText = "Greetings,\n\nBelow the text is a list of the Books You have ordered from us. Also, please find the attached ZIP file.\n";
        for (int i = 0; i < orderedBooks.size(); i++) {
            Book book = orderedBooks.get(i);
            bodyText += "- Book " + (i+1) + ": " + book + "\n\n";
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setSentDate(new Date());

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(bodyText);

        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is the attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachmentPath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(attachmentName);
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);

        // Send message
        Transport.send(message);

        //System.out.println("** Email sent successfully with attachment.");
    }
}
