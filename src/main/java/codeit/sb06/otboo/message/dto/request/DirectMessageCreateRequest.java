package codeit.sb06.otboo.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DirectMessageCreateRequest(
        @NotNull
        UUID receiverId,
        @NotNull
        UUID senderId,
        @NotBlank
        String content
) {
}
