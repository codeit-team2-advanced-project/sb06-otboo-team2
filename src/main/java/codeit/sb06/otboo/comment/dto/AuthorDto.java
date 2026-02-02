package codeit.sb06.otboo.comment.dto;

import codeit.sb06.otboo.user.entity.User;
import java.util.UUID;

public record AuthorDto(
    UUID userId,
    String name,
    String profileImageUrl
) {
  public static AuthorDto of(User user){
    return new AuthorDto(
        user.getId(),
        user.getName(),
        user.getProfileImageUrl()
    );
  }
}
