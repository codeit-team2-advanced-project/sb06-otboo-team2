package codeit.sb06.otboo.notification.dto;

import codeit.sb06.otboo.notification.enums.NotificationLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationDto(
        UUID id,
        LocalDateTime createdAt,
        UUID receiverId,
        String title,
        String content,
        NotificationLevel level
) {
}
