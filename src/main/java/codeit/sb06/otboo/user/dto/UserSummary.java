package codeit.sb06.otboo.user.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserSummary (
        UUID userId,
        String name,
        String profileImageUrl
){
}
