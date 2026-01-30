package codeit.sb06.otboo.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FeedLikedEvent(
        UUID targetId,
        String feedTitle,
        String likerName
) {
}
