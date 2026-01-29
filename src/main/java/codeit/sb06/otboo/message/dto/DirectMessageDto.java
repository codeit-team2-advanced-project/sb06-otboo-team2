package codeit.sb06.otboo.message.dto;

import codeit.sb06.otboo.user.dto.UserSummary;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DirectMessageDto(
        UUID id,
        LocalDateTime createdAt,
        UserSummary sender,
        UserSummary receiver,
        String content
) {
}
