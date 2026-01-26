package codeit.sb06.otboo.dto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}
