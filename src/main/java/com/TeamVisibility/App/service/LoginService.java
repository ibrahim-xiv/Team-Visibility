package com.TeamVisibility.App.service;

import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent()) {
            // Hinweis: In einer echten App nutzt man BCrypt zum Passwort-Vergleich!
            return user.get().getPasswordHash().equals(password);
        }
        return false;
    }
}