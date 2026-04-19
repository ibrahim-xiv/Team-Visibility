package com.TeamVisibility.App.service;

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
        if (!meeting.isNonProfit()) {
            throw new IllegalArgumentException("Das Treffen muss zwingend Non-Profit sein!");
        }
        return meetingRepository.save(meeting);
    }
}