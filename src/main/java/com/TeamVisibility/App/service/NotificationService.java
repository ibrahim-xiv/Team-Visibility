package com.TeamVisibility.App.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.TeamVisibility.App.model.Notification;
import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.repository.NotificationRepository;
import com.TeamVisibility.App.repository.UserRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository nr, UserRepository ur) {
        this.notificationRepository = nr;
        this.userRepository = ur;
    }

    public void notifyAllUsersAboutNewEvent(Long creatorId, Long meetingId, String eventTitle) {
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u.getId().equals(creatorId)) continue;
            Notification n = new Notification();
            n.setUserId(u.getId());
            n.setMeetingId(meetingId);
            n.setMessage("Neues Event in deiner Nähe: " + eventTitle);
            notificationRepository.save(n);
        }
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }
}
