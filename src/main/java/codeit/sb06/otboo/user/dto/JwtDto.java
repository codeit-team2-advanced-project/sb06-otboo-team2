package codeit.sb06.otboo.user.dto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}
