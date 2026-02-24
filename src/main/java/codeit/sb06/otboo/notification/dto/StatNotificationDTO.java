package codeit.sb06.otboo.notification.dto;

import java.util.UUID;

public record StatNotificationDTO(
        UUID receiverId,
        long feedCount,
        long feedLikeCount,
        long feedCommentCount
) {
}
