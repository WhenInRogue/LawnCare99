package com.WhenInRogue.LawnCare99.models;

import com.WhenInRogue.LawnCare99.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity

// @AllArgsConstructor as the name suggests, generates a constructor that includes a parameter for every field in the class,
// regardless of whether those fields are declared as final or not.
@AllArgsConstructor

//@NoArgsConstructor will generate a constructor with no parameters.
// If this is not possible (because of final fields), a compiler error will result instead
@NoArgsConstructor
@Table(name = "users")

//@Data annotation is a shortcut that bundles several other Lombok annotations
//such as @Getters, @Setters, @ToString, etc.
@Data

//@Builder avoids this ugly boilerplate code -->  New Person person(); person.setfirstname ="your first name";
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "PhoneNumber is required")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    private List<SupplyTransaction> supplyTransactions;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
