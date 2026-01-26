package codeit.sb06.otboo.dto;

import codeit.sb06.otboo.entity.Users;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String name,
    LocalDateTime createdAt,
    String role,
    boolean isLocked
) {

    public static UserDto from(Users user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getCreatedAt(),
            user.getRole().name(),
            user.isLocked()
        );
    }

}
