package codeit.sb06.otboo.notification.event;

import codeit.sb06.otboo.user.entity.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RoleUpdatedEvent(
        UUID targetId,
        Role role
) {
}
