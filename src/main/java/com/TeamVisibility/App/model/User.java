package com.TeamVisibility.App.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Application user.
 *
 * Merged from feature/registration + feature/login.
 *
 * Conflicts resolved:
 *   - Package: was com.visibility.model on disk under com/TeamVisibility/App/model
 *     (would not compile). Standardized to com.TeamVisibility.App.model.
 *   - Lombok @Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor stripped.
 *     Lombok was never declared in pom.xml on any branch, so the original
 *     User.java failed to compile. Replaced with explicit constructors and
 *     getters/setters to avoid adding a dependency.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    public User() {
    }

    public User(String username, String firstName, String lastName,
                String email, String passwordHash) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // --- getters / setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Boolean getVerified() { return isVerified; }
    public void setVerified(Boolean verified) { this.isVerified = verified; }
}
