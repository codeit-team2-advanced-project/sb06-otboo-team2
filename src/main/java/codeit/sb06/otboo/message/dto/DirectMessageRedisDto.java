package codeit.sb06.otboo.message.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DirectMessageRedisDto(
        UUID receiverId,
        UUID senderId,
        String content,
        String destination
) {
}
