package com.example.hospitalmanage.service.impl;

import com.sun.mail.smtp.SMTPTransport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.example.hospitalmanage.constant.EmailConstant.*;

@Service
@AllArgsConstructor
public class EmailService {


    public void sendMessage(String firstname, String lastname, String email)
            throws MessagingException {
        Message message = creatMail(firstname,email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport();
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private javax.mail.Message creatMail(String firstname, String email)
            throws MessagingException {
       javax.mail.Message message = new MimeMessage(getEmailSession());
       message.setFrom(new InternetAddress(FROM_EMAIL));
       message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
       message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(CC_EMAIL, false));
       message.setSubject(EMAIL_SUBJECT);
       message.setText(
               "Hello " +  firstname +
                       ", \n \n Your registration successfull: \n \n" +
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
