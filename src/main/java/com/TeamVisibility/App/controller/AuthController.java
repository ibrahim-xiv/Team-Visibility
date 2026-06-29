package com.TeamVisibility.App.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TeamVisibility.App.dto.ApiMessageResponse;
import com.TeamVisibility.App.dto.LoginRequest;
import com.TeamVisibility.App.dto.RegisterRequest;
import com.TeamVisibility.App.dto.ResendVerificationCodeRequest;
import com.TeamVisibility.App.dto.UserProfileResponse;
import com.TeamVisibility.App.dto.VerifyRegistrationRequest;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SESSION_USER_ID = "userId";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserProfileResponse register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return UserProfileResponse.from(user);
    }

    @PostMapping("/resend-verification-code")
    public ApiMessageResponse resendVerificationCode(@RequestBody ResendVerificationCodeRequest request) {
        userService.resendVerificationCode(request);
        return new ApiMessageResponse("Ein neuer Verifizierungscode wurde per Email gesendet.");
    }

    @PostMapping("/verify-registration")
    public UserProfileResponse verifyRegistration(@RequestBody VerifyRegistrationRequest request) {
        User user = userService.verifyRegistration(request);
        return UserProfileResponse.from(user);
    }

    @PostMapping("/login")
    public UserProfileResponse login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request);
        session.setAttribute(SESSION_USER_ID, user.getId());
        return UserProfileResponse.from(user);
    }

    @PostMapping("/logout")
    public ApiMessageResponse logout(HttpSession session) {
        session.invalidate();
        return new ApiMessageResponse("Abgemeldet.");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(UserProfileResponse.from(userService.findById(id)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiMessageResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ApiMessageResponse(exception.getMessage()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> body) {
        try {
            String email = body.get("email");
            String newPassword = body.get("password");
            com.TeamVisibility.App.model.User user = userService.findByEmail(email);
            user.setPasswordHash(new com.TeamVisibility.App.service.PasswordService().hash(newPassword));
            user.setVerified(true);
            userService.saveUser(user);
            return ResponseEntity.ok(java.util.Map.of("message", "Passwort zurückgesetzt"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> body) {
        try {
            String email = body.get("email");
            com.TeamVisibility.App.model.User user = userService.findByEmail(email);
            String code = String.format("%05d", new java.security.SecureRandom().nextInt(100000));
            user.setVerificationCode(code);
            userService.saveUser(user);
            userService.sendForgotPasswordCode(email, code);
            return ResponseEntity.ok(java.util.Map.of("message", "Code gesendet"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-with-code")
    public ResponseEntity<?> resetWithCode(@RequestBody java.util.Map<String, String> body) {
        try {
            String email = body.get("email");
            String code = body.get("code");
            String newPassword = body.get("password");
            com.TeamVisibility.App.model.User user = userService.findByEmail(email);
            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Falscher Code"));
            }
            user.setPasswordHash(new com.TeamVisibility.App.service.PasswordService().hash(newPassword));
            user.setVerificationCode(null);
            user.setVerified(true);
            userService.saveUser(user);
            return ResponseEntity.ok(java.util.Map.of("message", "Passwort erfolgreich zurückgesetzt"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

}