package com.TeamVisibility.App.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamVisibility.App.dto.*;
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
        if (!(userId instanceof Long id)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(UserProfileResponse.from(userService.findById(id)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiMessageResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiMessageResponse(ex.getMessage()));
    }
}