package com.WhenInRogue.LawnCare99.repositories;

import com.WhenInRogue.LawnCare99.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
