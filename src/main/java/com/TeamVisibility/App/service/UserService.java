package com.TeamVisibility.App.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.UserRepository;

/**
 * Combined registration + login service.
 *
 * Merged from:
 *   - feature/registration RegistrationService.registerUser(...)
 *   - feature/login        LoginService.login(...)
 *
 * Conflicts resolved:
 *   - Package was com.visibility.service on disk under com/TeamVisibility/App/service.
 *     Standardized to com.TeamVisibility.App.service.
 *   - feature/login UserController called registrationService.register(user)
 *     while RegistrationService only defined registerUser(user). Both names
 *     are kept as a thin wrapper here so neither call site breaks.
 *
 * Known issue (left intentionally per "no JWT / no rewrite" instructions):
 *   - Passwords are stored and compared in plain text. Add BCrypt before
 *     anything resembling production use.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (user.getEmail() != null
            && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getUsername() != null
            && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        // Plain-text persistence - documented limitation, NOT for production.
        return userRepository.save(user);
    }

    /** Alias preserved for backwards compatibility with feature/login UserController. */
    public User register(User user) {
        return registerUser(user);
    }

    /**
     * Try to log in by username OR email + password.
     * Returns the matched user on success, or empty on failure.
     */
    public Optional<User> login(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return Optional.empty();
        }
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }
        return userOpt.filter(u -> password.equals(u.getPasswordHash()));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
