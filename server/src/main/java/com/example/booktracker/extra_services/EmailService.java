package com.example.booktracker.extra_services;

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
     * Sends a verification email to the specified recipient with retry logic.
     *
     * This method attempts to send a verification email to the given recipient address.
     * It retries sending the email up to a maximum number of attempts if a {@link MailException}
     * occurs. If the email fails to send after the specified number of attempts, the exception
     * is propagated to notify the caller of the failure.
     *
     * @param recipientEmail The email address of the recipient to whom the verification email will be sent.
     * @param subject The subject of the verification email.
     * @param body The body content of the verification email, which typically includes the verification link.
     *
     * @throws MailException If an error occurs while sending the email and the maximum number of retry attempts is reached.
     */
    public void sendVerificationEmail(String recipientEmail, String subject, String body) {
        int maxTries = 2;
        int attempt = 0;
        boolean emailSent = false;

        while (attempt <= maxTries && !emailSent) {
            try {
                send(recipientEmail, subject, body);
                emailSent = true;
            }
            catch (MailException e) {
                attempt++;
                if (attempt > maxTries) {
                    throw e;  // Propagate exception after all attempts fail
                }
            }
        }
    }


    /**
     * Sends an email to the specified recipient.
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
    private void send(String recipientEmail, String subject, String body) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom("rayhuntr1@gmail.com");
        simpleMailMessage.setTo(recipientEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);
    }

}
