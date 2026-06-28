package com.TeamVisibility.App.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.User;

@Service
public class VerificationEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;

    public VerificationEmailService(
        ObjectProvider<JavaMailSender> mailSenderProvider,
        @Value("${visibility.mail.from:no-reply@visibility.local}") String fromAddress
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationCode(User user) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            LOGGER.warn(
                "Kein Mail-Sender konfiguriert. Verifizierungscode fuer {} lautet {}.",
                user.getEmail(),
                user.getVerificationCode()
            );
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Dein Visibility-Verifizierungscode");
        message.setText("Hallo " + user.getFirstName() + ",\n\n"
            + "dein Verifizierungscode fuer Visibility lautet: " + user.getVerificationCode() + "\n\n"
            + "Gib diesen Code in der App ein, um deinen Account zu aktivieren.\n\n"
            + "Viele Gruesse\nDein Visibility-Team");

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            LOGGER.error(
                "Verifizierungscode konnte nicht per Email an {} gesendet werden. Code: {}",
                user.getEmail(),
                user.getVerificationCode(),
                exception
            );
        }
    }
}
