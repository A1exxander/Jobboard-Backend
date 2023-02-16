package io.Jobboard.Mail;

import io.Jobboard.DB.JDBCUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mail {

    Session session = null;
    String sender = "jose.rodriges@yahoo.com";  // Our login credentials
    String senderPassword = "lqlapbhblyfszynm";

    public Mail() throws MessagingException {

        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.mail.yahoo.com");
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, senderPassword);
            }
        });

    }

    public void sendMessage(String recipient, EmailTemplates messageType) throws MessagingException, SQLException {
        Message m = createMessage(recipient, messageType);
        if (m == null){
            return;
        }
        Transport.send(m);
    }

    public Message createMessage(String recipient, EmailTemplates messageType) throws MessagingException, SQLException { // Probably better to just call functions such as sendTokenEmail() directly as this is control coupling but then we'd be repeating the message.setFrom and recipient everytime

        Message message = new MimeMessage(session); // Message is an ADT or interface
        message.setFrom(new InternetAddress(sender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        String emailSubject = "";
        String emailBody = "";

        switch(messageType){

            case WELCOME:{
                emailSubject = "Welcome to JobBoard.io!";
                emailBody = "We are glad to have you onboard," + recipient + ". If you wish to learn more about JobBoard.io, please click here : (link) ";
                break;
            }
            case RESET_PASSWORD:{
                JDBCUtil db = JDBCUtil.getInstance();
                ResultSet rs = db.executeQuery("SELECT TOKEN FROM PASSWORD_RESET_TOKENS WHERE EMAIL = \"" + recipient + "\";");
                String token = rs.getString("Token");
                if (token == null){
                    return null;
                }
                emailSubject = "JobBoard.io Password Reset";
                emailBody = "Your password reset token is \"" + token + "\". It will expire in 5 minutes";
                break;
            }
            case SEND_PASSWORD:{
                JDBCUtil db = JDBCUtil.getInstance();
                ResultSet rs = db.executeQuery("SELECT PASSWORD FROM USERS WHERE EMAIL = \"" + recipient + "\";");
                String password = rs.getString("Password");
                if (password == null){
                    return null;
                }
                emailSubject = "JobBoard.io Password Reset";
                emailBody = "Your password is \"" + password + "\". Our system does not yet accept password resets.";
                break;
            }
            default: {
                return null;
            }

        }

        message.setSubject(emailSubject);
        message.setText(emailBody);
        return message;

    }

}
