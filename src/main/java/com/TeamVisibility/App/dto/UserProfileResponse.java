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
    Integer notifRadius,
    String avatar,
    String bio,
    String locationName,
    String interests,
    int followerCount
) {
    public static UserProfileResponse from(User user) {
        int fc = 0;
        if (user.getFollowers() != null && !user.getFollowers().isEmpty()) {
            fc = user.getFollowers().split(",").length;
        }
        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().name(),
            Boolean.TRUE.equals(user.isVerified()),
            Boolean.TRUE.equals(user.isHighContrast()),
            user.getNotifRadius(),
            user.getAvatar(),
            user.getBio(),
            user.getLocationName(),
            user.getInterests(),
            fc
        );
    }
}
