package com.example.hospitalmanage.service.impl;

import com.sun.mail.smtp.SMTPTransport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.example.hospitalmanage.constant.EmailConstant.*;

@Service
public class EmailService {

    public EmailService() { }

    public void sendMessageRegistartion(String firstname, String lastname, String username, String email)
            throws MessagingException {
        Message message = creatProfile(firstname, lastname, username, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendMessageUpdateProfile(String firstname, String lastname,String username,String email)
            throws MessagingException {
        Message message = mailUpdateProfile(firstname, lastname, username, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendMessageUpdateProfileImage(String firstname, String lastname,String username,String email)
            throws MessagingException {
        Message message = updateImageProfile(firstname, lastname, username, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendMessageUpdatePasswordProfile(String firstname, String lastname, String newPass, String email)
            throws MessagingException {
        Message message = updatePasswordProfile(firstname, lastname, newPass, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendMessageDeleteAccount(String firstname, String lastname, String username, String email)
            throws MessagingException {
        Message message = deleteProfile(firstname, lastname, username, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message deleteProfile(String firstname, String lastname, String username, String email)
            throws MessagingException {
        javax.mail.Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(
                "Hello " + firstname + " " + lastname +
                        ", \n \n Your delete account: " + username + " is successfull: \n \n" +
                        "The support Team Hospital ");
        message.setSentDate(new Date());
        return message;
    }

    private Message updatePasswordProfile(String firstname, String lastname, String newPass, String email)
            throws MessagingException {
        javax.mail.Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(
                "Hello " + firstname + " " + lastname +
                        ", \n \n Your update password is successfull: \n \n" +
                        " \n \n  new password : " + newPass + "\n \n " +
                        "The support Team Hospital ");
        message.setSentDate(new Date());
        return message;
    }

    private javax.mail.Message updateImageProfile(String firstname, String lastname,String username,String email)
            throws MessagingException {
        javax.mail.Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(
                "Hello " + firstname + " " + lastname +
                        ", \n \n Your update image successfull: \n \n" +
                        "The support Team Hospital ");
        message.setSentDate(new Date());
        return message;

    }

    private javax.mail.Message creatProfile(String firstname, String lastname,String username,String email)
            throws MessagingException {
        javax.mail.Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(
                "Hello " + firstname + " " + lastname +
                        ", \n \n Your username : " + username + "\n registration successfull! \n \n" +
                        "The support Team Hospital ");
        message.setSentDate(new Date());
        return message;
    }

    private javax.mail.Message mailUpdateProfile(String firstname, String lastname,String username,String email)
            throws MessagingException {
        javax.mail.Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(
                "Hello " + firstname +
                        ", \n \n Your profile successfull update: \n \n" +
                        "The support Team Hospital ");
        message.setSentDate(new Date());
        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLED, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

}


