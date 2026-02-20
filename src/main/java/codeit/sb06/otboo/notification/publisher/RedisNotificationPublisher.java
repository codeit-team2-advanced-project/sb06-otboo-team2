package codeit.sb06.otboo.notification.publisher;

import codeit.sb06.otboo.notification.dto.NotificationDto;

import java.util.List;

public interface RedisNotificationPublisher {
    void publish(NotificationDto dto);

    void publishAll(List<NotificationDto> dtoList);
}
