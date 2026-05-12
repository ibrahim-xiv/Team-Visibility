package com.TeamVisibility.App.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TeamVisibility.App.model.Favorite;
import com.TeamVisibility.App.repository.FavoriteRepository;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Favorite addFavorite(Long userId, Long meetingId) {
        return favoriteRepository
            .findByUserIdAndMeetingId(userId, meetingId)
            .orElseGet(() -> favoriteRepository.save(new Favorite(userId, meetingId)));
    }

    @Transactional
    public void removeFavorite(Long userId, Long meetingId) {
        favoriteRepository.deleteByUserIdAndMeetingId(userId, meetingId);
    }

    public List<Favorite> getFavoritesForUser(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
}
