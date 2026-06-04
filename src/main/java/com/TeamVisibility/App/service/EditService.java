package com.TeamVisibility.App.service;

import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EditService {

    private final UserRepository userRepository;

    public EditService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void edit(String username, String firstName, String lastName, String password){
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()){
            user.setFirstname(firstName);
            user.setLastName(lastName);
            user.setPasswordHash(password);

            userRepository.save(user);
        }
        else{
            throw new RuntimeException("User does not Exist");
        }
    }
}
