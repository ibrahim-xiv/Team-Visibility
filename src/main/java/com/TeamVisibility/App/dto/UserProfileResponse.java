package com.TeamVisibility.App.dto;

import com.TeamVisibility.App.model.User;

public record UserProfileResponse(
    Long id,
    String username,
    String firstName,
    String lastName,
    String email,
    String role,
    boolean verified,
    boolean highContrast,
    Integer notifRadius
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().name(),
            user.isVerified(),
            user.isHighContrast(),
            user.getNotifRadius()
        );
    }
}
