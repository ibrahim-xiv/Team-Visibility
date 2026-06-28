package com.TeamVisibility.App.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User registerUser(User user) {
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email bereits vergeben");
        }
        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username bereits vergeben");
        }
        user.setVerified(false);
        return userRepository.save(user);
    }

    public User register(User user) {
        return registerUser(user);
    }

    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Kein Konto mit dieser E-Mail gefunden"));

        String code = String.format("%05d", new Random().nextInt(100000));
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendVerificationCode(email, code);
    }

    public void verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Kein Konto mit dieser E-Mail gefunden"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Ungültiger Code");
        }
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Code abgelaufen. Bitte neu anfordern.");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    public Optional<User> login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) return Optional.empty();

        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) userOpt = userRepository.findByEmail(usernameOrEmail);

        return userOpt.filter(u -> password.equals(u.getPasswordHash()));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}