package codeit.sb06.otboo.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Profile(
    String nickname,
    @JsonProperty("profile_image_url") String profileImageUrl
) {

}
