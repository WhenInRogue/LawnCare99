package com.WhenInRogue.LawnCare99.services;

public interface EmailService {
    void sendPasswordResetEmail(String recipientEmail, String resetLink);
}
