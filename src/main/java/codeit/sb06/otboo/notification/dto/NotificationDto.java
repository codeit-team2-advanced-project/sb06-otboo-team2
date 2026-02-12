package codeit.sb06.otboo.notification.dto;

import codeit.sb06.otboo.notification.enums.NotificationLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationDto(
        UUID id,
        LocalDateTime createdAt,
        UUID receiverId,
        String title,
        String content,
        NotificationLevel level
) {
}
