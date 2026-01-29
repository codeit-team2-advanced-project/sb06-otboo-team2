package codeit.sb06.otboo.notification.service;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.NotificationLevel;

import java.util.UUID;

public interface NotificationService {

    NotificationDto create(UUID receiverId, String title, String content, NotificationLevel level);

    void deleteById(UUID notificationId);
}
