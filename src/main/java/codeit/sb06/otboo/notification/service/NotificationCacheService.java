package codeit.sb06.otboo.notification.service;

import codeit.sb06.otboo.notification.dto.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationCacheService {

    void save(UUID userId, NotificationDto data);

    List<NotificationDto> getRecentNotifications(UUID userId);

    List<NotificationDto> getNotificationsAfter(UUID userId, String lastEventId);
}
