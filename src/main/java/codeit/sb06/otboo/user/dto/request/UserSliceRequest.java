package codeit.sb06.otboo.user.dto.request;

public record UserSliceRequest(
    String cursor,
    String idAfter,
    int limit,
    String sortBy,
    String sortDirection,
    String emailLike,
    String roleEqual,
    Boolean locked
) {

}
