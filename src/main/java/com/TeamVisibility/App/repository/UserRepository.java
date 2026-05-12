package com.TeamVisibility.App.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TeamVisibility.App.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    /**
     * From feature/login: LoginService called userRepository.findByUsername(...)
     * but the original UserRepository only declared findByEmail. Added here
     * to make the login flow compile.
     */
    Optional<User> findByUsername(String username);
}
