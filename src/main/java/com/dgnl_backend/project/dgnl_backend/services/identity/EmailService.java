package com.dgnl_backend.project.dgnl_backend.services.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dgnl_backend.project.dgnl_backend.schemas.Email;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public String sendEmail(Email emailDetails){
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            MimeMessage mailMessage
                = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "UTF-8");

            // Setting up necessary details
            helper.setFrom(sender);
            helper.setTo(emailDetails.recipient());
            helper.setText(emailDetails.content(), true);
            helper.setSubject(emailDetails.subject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }
}
