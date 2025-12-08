package com.WhenInRogue.LawnCare99.services.impl;

import com.WhenInRogue.LawnCare99.dtos.ForgotPasswordRequest;
import com.WhenInRogue.LawnCare99.dtos.LoginRequest;
import com.WhenInRogue.LawnCare99.dtos.RegisterRequest;
import com.WhenInRogue.LawnCare99.dtos.ResetPasswordRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.UserDTO;
import com.WhenInRogue.LawnCare99.enums.UserRole;
import com.WhenInRogue.LawnCare99.exceptions.InvalidCredentialsException;
import com.WhenInRogue.LawnCare99.exceptions.NotFoundException;
import com.WhenInRogue.LawnCare99.models.PasswordResetToken;
import com.WhenInRogue.LawnCare99.models.User;
import com.WhenInRogue.LawnCare99.repositories.PasswordResetTokenRepository;
import com.WhenInRogue.LawnCare99.repositories.UserRepository;
import com.WhenInRogue.LawnCare99.security.JwtUtils;
import com.WhenInRogue.LawnCare99.services.EmailService;
import com.WhenInRogue.LawnCare99.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.reset.base-url}")
    private String resetBaseUrl;


    @Override
    public Response registerUser(RegisterRequest registerRequest) {

        UserRole role = UserRole.MANAGER;

        if (registerRequest.getRole() != null) {
            role = registerRequest.getRole();
        }

        User userToSave = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(role)
                .build();

        userRepository.save(userToSave);

        return Response.builder()
                .status(200)
                .message("User was successfully registered")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email Not Found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password Does Not Match");
        }
        String token = jwtUtils.generateToken(user.getEmail());

        return Response.builder()
                .status(200)
                .message("User Logged in Successfully")
                .role(user.getRole())
                .token(token)
                .expirationTime("6 months")
                .build();
    }

    @Override
    public Response getAllUsers() {

        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        users.forEach(user -> user.setSupplyTransactions(null));
        users.forEach(user -> user.setEquipmentTransactions(null));
        users.forEach(user -> user.setMaintenanceRecords(null));

        List<UserDTO> userDTOS = modelMapper.map(users, new TypeToken<List<UserDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .users(userDTOS)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not Found"));

        user.setSupplyTransactions(null);
        user.setEquipmentTransactions(null);
        user.setMaintenanceRecords(null);

        return user;
    }

    @Override
    public Response getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setSupplyTransactions(null);
        userDTO.setEquipmentTransactions(null);
        userDTO.setMaintenanceRecords(null);

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public Response updateUser(Long id, UserDTO userDTO) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getPhoneNumber() != null) existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if (userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("User successfully updated")
                .build();
    }

    @Override
    public Response deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        userRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("User successfully Deleted")
                .build();

    }

    @Override
    public Response getUserSupplyTransactions(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.getSupplyTransactions().forEach(supplyTransactionDTO -> {
            supplyTransactionDTO.setUser(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public Response getUserEquipmentTransactions(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.getEquipmentTransactions().forEach(equipmentTransactionDTO -> {
            equipmentTransactionDTO.setUser(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();

    }

    @Override
    public Response getUserMaintenanceRecords(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.getMaintenanceRecords().forEach(maintenanceRecordDTO -> {
            maintenanceRecordDTO.setUser(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public Response requestPasswordReset(ForgotPasswordRequest forgotPasswordRequest) {

        userRepository.findByEmail(forgotPasswordRequest.getEmail()).ifPresent(user -> {
            passwordResetTokenRepository.deleteAllByUser(user);

            PasswordResetToken token = PasswordResetToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .build();

            passwordResetTokenRepository.save(token);

            emailService.sendPasswordResetEmail(user.getEmail(), buildResetLink(token.getToken()));
        });

        return Response.builder()
                .status(200)
                .message("If an account exists for that email, a reset link was sent")
                .build();
    }

    @Override
    public Response resetPassword(ResetPasswordRequest resetPasswordRequest) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new NotFoundException("Reset token is invalid"));

        if (passwordResetToken.isExpired()) {
            throw new InvalidCredentialsException("Reset token has expired");
        }

        if (passwordResetToken.isUsed()) {
            throw new InvalidCredentialsException("Reset token has already been used");
        }

        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(passwordResetToken);

        return Response.builder()
                .status(200)
                .message("Password reset successfully")
                .build();
    }

    private String buildResetLink(String token) {
        String separator = resetBaseUrl.contains("?") ? "&" : "?";
        return resetBaseUrl + separator + "token=" + token;
    }
}
