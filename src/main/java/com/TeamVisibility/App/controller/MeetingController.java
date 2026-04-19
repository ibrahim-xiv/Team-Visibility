package com.TeamVisibility.App.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.TeamVisibility.App.model.Meeting;
import com.TeamVisibility.App.service.MeetingService;

@Controller
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // Zeigt das Formular an
    @GetMapping("/treffen/neu")
    public String showForm(Model model) {
        model.addAttribute("meeting", new Meeting());
        return "meeting-form"; 
    }

    // Nimmt die Daten vom Formular entgegen
    @PostMapping("/treffen/neu")
    public String submitForm(@ModelAttribute Meeting meeting, Model model) {
        try {
            meetingService.createMeeting(meeting);
            model.addAttribute("successMessage", "Treffen erfolgreich erstellt!");
            model.addAttribute("meeting", new Meeting()); // Formular leeren
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "meeting-form";
    }
}