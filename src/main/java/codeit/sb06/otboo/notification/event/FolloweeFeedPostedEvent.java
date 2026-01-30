package codeit.sb06.otboo.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FolloweeFeedPostedEvent(
        UUID targetId,
        String followeeName,
        String feedTitle
) {
}
