package codeit.sb06.otboo.user.dto.response;

import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Slice;

public record UserDtoCursorResponse(
    List<UserDto> data,
    String nextCursor,
    String nextIdAfter,
    boolean hasNext,
    long totalCount,
    String sortBy,
    String sortDirection
) {

    public static UserDtoCursorResponse from(Slice<User> slice, UserSliceRequest request) {
        List<UserDto> data = slice.getContent().stream()
            .map(UserDto::from)
            .toList();

        String sortBy = request.sortBy() == null ? "createdAt" : request.sortBy();
        String sortDirection = request.sortDirection() == null ? "DESC" : request.sortDirection();

        String nextCursor = null;
        String nextIdAfter = null;
        if (slice.hasNext() && !slice.getContent().isEmpty()) {
            User last = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = cursorValue(last, sortBy);
            nextIdAfter = last.getId() == null ? null : last.getId().toString();
        }

        return new UserDtoCursorResponse(
            data,
            nextCursor,
            nextIdAfter,
            slice.hasNext(),
            slice.getNumberOfElements(),
            sortBy,
            sortDirection
        );
    }

    private static String cursorValue(User user, String sortBy) {
        if ("email".equalsIgnoreCase(sortBy)) {
            return user.getEmail();
        }
        if ("role".equalsIgnoreCase(sortBy)) {
            return user.getRole() == null ? null : user.getRole().name();
        }
        if ("updatedAt".equalsIgnoreCase(sortBy)) {
            LocalDateTime updatedAt = user.getUpdatedAt();
            return updatedAt == null ? null : updatedAt.toString();
        }
        if ("id".equalsIgnoreCase(sortBy)) {
            return user.getId() == null ? null : user.getId().toString();
        }
        LocalDateTime createdAt = user.getCreatedAt();
        return createdAt == null ? null : createdAt.toString();
    }
}
