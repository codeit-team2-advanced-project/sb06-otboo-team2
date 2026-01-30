package codeit.sb06.otboo.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ClothesAttributeAddedEvent(
        UUID targetId,
        String attributeName
) {
}
