package codeit.sb06.otboo.notification.mapper;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {

        return NotificationDto.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .receiverId(notification.getReceiverId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .level(notification.getLevel())
                .build();
    }
}
