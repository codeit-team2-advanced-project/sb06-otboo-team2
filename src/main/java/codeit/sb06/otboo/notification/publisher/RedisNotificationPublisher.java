package codeit.sb06.otboo.notification.publisher;

import codeit.sb06.otboo.notification.dto.NotificationDto;

public interface RedisNotificationPublisher {
    void publish(NotificationDto dto);
}
