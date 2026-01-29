package codeit.sb06.otboo.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FeedCommentedEvent(
        UUID targetId,
        String commenterName,
        String feedTitle,
        String content
) {
}
