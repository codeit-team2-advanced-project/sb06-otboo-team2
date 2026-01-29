package codeit.sb06.otboo.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FollowedEvent(
        UUID targetId,
        String followerName
) {
}
