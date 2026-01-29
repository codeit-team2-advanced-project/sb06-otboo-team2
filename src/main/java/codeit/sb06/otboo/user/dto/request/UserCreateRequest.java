package codeit.sb06.otboo.user.dto.request;

public record UserCreateRequest(
    String name,
    String email,
    String password
) {

}
