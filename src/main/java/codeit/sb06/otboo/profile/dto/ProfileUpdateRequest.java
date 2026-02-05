package codeit.sb06.otboo.profile.dto;

import java.util.Map;

public record ProfileUpdateRequest(
    String name,
    String gender,
    String birthDate,
    LocationDto locationDto,
    int temperatureSensitivity
) {

}
