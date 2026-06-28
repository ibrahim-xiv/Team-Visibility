package com.TeamVisibility.App.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.TeamVisibility.App.model.Meeting;
import com.TeamVisibility.App.repository.MeetingRepository;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting createMeeting(Meeting meeting) {
        if (meeting.getNonProfit() == null || !meeting.getNonProfit()) {
            throw new IllegalArgumentException(
                "Das Treffen muss zwingend Non-Profit sein!");
        }
        return meetingRepository.save(meeting);
    }

    public List<Meeting> findAll() {
        return meetingRepository.findAll();
    }

    public Meeting findById(Long id) {
        return meetingRepository.findById(id).orElse(null);
    }

    public List<Meeting> findByCategoryId(Long categoryId) {
        return meetingRepository.findByCategoryId(categoryId);
    }

    public List<Meeting> findByCreatorId(Long creatorId) {
        return meetingRepository.findByCreatorId(creatorId);
    }

    public Meeting update(Long id, Meeting incoming) {
        Meeting existing = meetingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
        if (incoming.getTitle() != null)        existing.setTitle(incoming.getTitle());
        if (incoming.getDescription() != null)  existing.setDescription(incoming.getDescription());
        if (incoming.getLocationName() != null) existing.setLocationName(incoming.getLocationName());
        if (incoming.getLat() != null)          existing.setLat(incoming.getLat());
        if (incoming.getLng() != null)          existing.setLng(incoming.getLng());
        if (incoming.getDateTime() != null)     existing.setDateTime(incoming.getDateTime());
        if (incoming.getCategoryId() != null)   existing.setCategoryId(incoming.getCategoryId());
        if (incoming.getNonProfit() != null)    existing.setNonProfit(incoming.getNonProfit());
        return meetingRepository.save(existing);
    }

    public void delete(Long id) {
        meetingRepository.deleteById(id);
    }
}
