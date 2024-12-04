package com.utn.interactiveconsortium.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class EmailService {

    @Value("${mail.username}")
    private String fromMail;

    @Autowired
    private JavaMailSender emailSender;

    private static final String EMPTY_STRING = "";

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("consorcio.interactivo.rca@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendMessageWithAttachment(String[] to, String subject, String text, String fileName, InputStream file) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromMail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text != null ? text : EMPTY_STRING);

        InputStreamSource source = new ByteArrayResource(file.readAllBytes());
        helper.addAttachment(fileName, source);

        emailSender.send(message);
    }


}
