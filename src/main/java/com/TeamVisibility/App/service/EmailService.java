package com.TeamVisibility.App.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class EmailService {

    @Value("${gmail.username}")
    private String gmailUsername;

    @Value("${gmail.app.password}")
    private String gmailAppPassword;

    public void sendVerificationCode(String toEmail, String code) {
        try {
            String auth = Base64.getEncoder().encodeToString(
                (gmailUsername + ":" + gmailAppPassword.replace(" ", "")).getBytes()
            );

            // Use Gmail SMTP via raw socket with JavaMail-style through HTTP isn't possible
            // Use Jakarta Mail (JavaMail) approach via Properties
            sendViaSMTP(toEmail, code);
        } catch (Exception e) {
            throw new RuntimeException("E-Mail Fehler: " + e.getMessage(), e);
        }
    }

    private void sendViaSMTP(String toEmail, String code) throws Exception {
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        final String user = gmailUsername;
        final String pass = gmailAppPassword.replace(" ", "");

        jakarta.mail.Session session = jakarta.mail.Session.getInstance(props,
            new jakarta.mail.Authenticator() {
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(user, pass);
                }
            });

        jakarta.mail.Message message = new jakarta.mail.internet.MimeMessage(session);
        message.setFrom(new jakarta.mail.internet.InternetAddress(user));
        message.setRecipients(jakarta.mail.Message.RecipientType.TO,
            jakarta.mail.internet.InternetAddress.parse(toEmail));
        message.setSubject("Dein Visibility Verifizierungscode");
        message.setContent(
            "<h2>Dein Code: <strong>" + code + "</strong></h2>" +
            "<p>Dieser Code ist 15 Minuten gültig.</p>",
            "text/html; charset=utf-8"
        );

        jakarta.mail.Transport.send(message);
    }
}