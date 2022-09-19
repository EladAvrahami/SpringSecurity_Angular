package com.supportportal.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
//IMPORT ALL EMAIL CONSTANTS WITH *
import static com.supportportal.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;


@Service
public class EmailService {


    public void sendNewPasswordEmail(String firstName,String password,String email) throws MessagingException {
        Message message=createEmail(firstName,password,email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER,USERNAME,PASSWORD);
        smtpTransport.sendMessage(message,message.getAllRecipients());//pass thi
        smtpTransport.close();
    }

    /**
     * @param firstName of the user i want to send the mail to
     * @param password the password the mail contain
     * @param email user email
     * @return session email with to username email that contain password
     * @throws MessagingException
     */
    private Message createEmail(String firstName,String password,String email) throws MessagingException {
        Message message =new MimeMessage(getEmailSession());//determine email session protocol
        message.setFrom(new InternetAddress(FROM_EMAIL)); // send from my app mail
        message.setRecipients(TO, InternetAddress.parse(email,false));//strict=true if i want email address in specifies format
        message.setRecipients(CC,InternetAddress.parse(CC_EMAIL,false));//
        message.setSubject(EMAIL_SUBJECT); //email subject
        //body of the email
        message.setText("Hello" + firstName+", \n \n Your new account password is: "+ password + "\n \n The Support Team");
        message.setSentDate(new Date());//email sent time.
        message.saveChanges();
        return message;
    }

        /*private Message createEmail(String firstName,String password,String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + ", \n \n Your new account password is: " + password + "\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;}*/



    /**
     * put all session properties together
     * @return the secure session that i need to send an email
     * (Session type come from javax-mail-dependency)
     */
    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST,GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH,true);//because we use authentication
        properties.put(SMTP_PORT,DEFAULT_PORT);//set port to default
        properties.put(SMTP_STARTTLS_ENABLE,true);//to make smtp protocol work
        properties.put(SMTP_STARTTLS_REQUIRED,true);//smtp to be must
        return Session.getInstance(properties,null);
    }
}
