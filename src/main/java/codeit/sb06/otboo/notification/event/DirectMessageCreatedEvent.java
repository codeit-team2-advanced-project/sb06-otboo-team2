package codeit.sb06.otboo.notification.event;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DirectMessageCreatedEvent(
        UUID targetId,
        String senderName,
        String content
) {
}
