package codeit.sb06.otboo.security.dto;

import codeit.sb06.otboo.user.dto.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}
