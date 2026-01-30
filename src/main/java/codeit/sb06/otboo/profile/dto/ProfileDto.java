package codeit.sb06.otboo.profile.dto;

import codeit.sb06.otboo.profile.entity.Profile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
//
@Builder(access = AccessLevel.PRIVATE)
public record ProfileDto(
    UUID userId,
    String name,
    String gender,
    LocalDateTime birthDate,
    List<String> locations,
    int temperatureSensitivity,
    String profileImageUrl
) {

    public static ProfileDto from(Profile profile){
        return ProfileDto.builder()
            .userId(profile.getUserId().getId())
            .name(profile.getName())
            .gender(profile.getGender())
            .birthDate(profile.getBirthday())
            .locations(profile.getLocations())
            .temperatureSensitivity(profile.getSensitivity())
            .profileImageUrl(profile.getImageUrl())
            .build();
    }
}
