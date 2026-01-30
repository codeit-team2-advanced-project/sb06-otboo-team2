package codeit.sb06.otboo.user.dto;

import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    LocalDateTime createdAt,
    String role,
    boolean isLocked
) {

    public static UserDto from(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getRole().name(),
            user.isLocked()
        );
    }

}
