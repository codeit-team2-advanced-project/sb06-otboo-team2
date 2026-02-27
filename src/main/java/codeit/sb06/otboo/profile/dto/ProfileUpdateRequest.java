package codeit.sb06.otboo.profile.dto;

public record ProfileUpdateRequest(
    String name,
    String gender,
    String birthDate,
    LocationDto location,
    int temperatureSensitivity
) {

}
