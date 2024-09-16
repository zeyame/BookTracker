package com.example.booktracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    /**
     * Sends a verification email to the specified recipient.
     *
     * This method creates a simple email message with the specified recipient, subject, and body content, and sends it
     * using the configured {@link JavaMailSender}. The sender's email address is set to "rayhuntr1@gmail.com".
     *
     * @param recipientEmail The email address of the recipient.
     * @param subject The subject of the email.
     * @param body The body content of the email.
     * @throws {@link MailException} If an error occurs while sending the email. This exception can include issues such as
     * network problems, invalid email addresses, or issues with the mail server configuration.
     */
    public void sendVerificationEmail(String recipientEmail, String subject, String body) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom("rayhuntr1@gmail.com");
        simpleMailMessage.setTo(recipientEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);
    }
}
