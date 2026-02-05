package codeit.sb06.otboo.profile.dto;

import codeit.sb06.otboo.profile.entity.Profile;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ProfileDto(
    UUID userId,
    String name,
    String gender,
    String birthDate,
    List<String> locations,
    int temperatureSensitivity,
    String profileImageUrl
) {

    public static ProfileDto from(Profile profile, List<String> locations) {
        return ProfileDto.builder()
            .userId(profile.getUserId().getId())
            .name(profile.getName())
            .gender(profile.getGender() == null ? null : profile.getGender().name())
            .birthDate(profile.getBirthday())
            .locations(locations)
            .temperatureSensitivity(profile.getSensitivity())
            .profileImageUrl(profile.getImageUrl())
            .build();
    }
}
