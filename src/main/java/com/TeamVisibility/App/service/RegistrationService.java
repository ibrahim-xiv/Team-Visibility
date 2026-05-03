package com.visibility.service;

import com.visibility.model.User;
import com.visibility.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        // In a real app, hash the password here before saving
        userRepository.save(user);
    }
}