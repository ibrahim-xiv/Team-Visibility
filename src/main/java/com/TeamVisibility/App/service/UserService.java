package com.TeamVisibility.App.service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TeamVisibility.App.dto.LoginRequest;
import com.TeamVisibility.App.dto.RegisterRequest;
import com.TeamVisibility.App.dto.ResendVerificationCodeRequest;
import com.TeamVisibility.App.dto.VerifyRegistrationRequest;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.model.UserRole;
import com.TeamVisibility.App.repository.UserRepository;

@Service
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).+$");
    private static final Pattern VERIFICATION_CODE_PATTERN = Pattern.compile("^\\d{5}$");

    private final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final EmailService emailService;

    public UserService(
        UserRepository userRepository,
        PasswordService passwordService,
        EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
    }

    @Transactional
    public User register(RegisterRequest request) {
        String firstName = requireText(request.firstName(), "Vorname ist erforderlich.");
        String lastName = requireText(request.lastName(), "Nachname ist erforderlich.");
        String email = normalizeEmail(request.email());
        String password = requireText(request.password(), "Passwort ist erforderlich.");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Bitte eine gültige Email eingeben.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Passwort muss mindestens 1 Großbuchstaben und 1 Zahl enthalten.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Diese Email ist bereits registriert.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(generateUsername(firstName, lastName, email));
        user.setPasswordHash(passwordService.hash(password));
        user.setRole(UserRole.USER);
        user.setVerified(false);
        user.setVerificationCode(generateVerificationCode());
        user.setHighContrast(false);

        User savedUser = userRepository.save(user);
        emailService.sendVerificationCode(savedUser.getEmail(), savedUser.getVerificationCode());
        return savedUser;
    }

    @Transactional
    public User resendVerificationCode(ResendVerificationCodeRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("Benutzer wurde nicht gefunden."));

        if (user.isVerified()) {
            throw new IllegalArgumentException("Dieser Account ist bereits aktiviert.");
        }

        user.setVerificationCode(generateVerificationCode());
        User savedUser = userRepository.save(user);
        emailService.sendVerificationCode(savedUser.getEmail(), savedUser.getVerificationCode());
        return savedUser;
    }

    @Transactional
    public User verifyRegistration(VerifyRegistrationRequest request) {
        String email = normalizeEmail(request.email());
        String verificationCode = requireText(request.verificationCode(), "Verifizierungscode ist erforderlich.");

        if (!VERIFICATION_CODE_PATTERN.matcher(verificationCode).matches()) {
            throw new IllegalArgumentException("Der Verifizierungscode muss aus 5 Zahlen bestehen.");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("Benutzer wurde nicht gefunden."));

        if (user.isVerified()) {
            return user;
        }

        if (!verificationCode.equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("Der Verifizierungscode ist falsch.");
        }

        user.setVerified(true);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        String password = requireText(request.password(), "Passwort ist erforderlich.");

        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("Email oder Passwort ist falsch."));

        if (!passwordService.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Email oder Passwort ist falsch.");
        }
        if (!user.isVerified()) {
            throw new IllegalArgumentException("Dein Account ist noch nicht aktiviert. Bitte gib zuerst den Verifizierungscode ein.");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Benutzer wurde nicht gefunden."));
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String normalizeEmail(String email) {
        return requireText(email, "Email ist erforderlich.").toLowerCase(Locale.ROOT);
    }

    private String generateVerificationCode() {
        return String.format("%05d", secureRandom.nextInt(100_000));
    }

    private String generateUsername(String firstName, String lastName, String email) {
        String base = (firstName + "." + lastName)
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9._-]", "");

        if (base.length() >= 3) {
            return base;
        }

        return email.substring(0, email.indexOf('@')).replaceAll("[^a-zA-Z0-9._-]", "");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("Kein Konto mit dieser E-Mail gefunden"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void sendForgotPasswordCode(String email, String code) {
        emailService.sendVerificationCode(email, code);
    }
}