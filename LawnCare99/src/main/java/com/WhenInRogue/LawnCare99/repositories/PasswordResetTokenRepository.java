package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.PasswordResetToken;
import com.WhenInRogue.LawnCare99.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteAllByUser(User user);
}
