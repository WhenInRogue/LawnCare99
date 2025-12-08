package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String defaultFromAddress;

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setFrom(defaultFromAddress);
        message.setSubject("Password Reset Request");
        message.setText("""
                We received a request to reset your LawnCare99 password.

                Click the link below to set a new password. This link expires in 15 minutes.

                %s

                If you did not request a reset, you can ignore this email.
                """.formatted(resetLink));

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send password reset email to {}", recipientEmail, ex);
            throw ex;
        }
    }
}
