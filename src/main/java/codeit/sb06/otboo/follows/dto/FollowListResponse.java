package codeit.sb06.otboo.follows.dto;

import java.util.List;
import java.util.UUID;

public record FollowListResponse(
    List<FollowDto> data,
    String nextCursor,
    UUID nextIdAfter,
    boolean hasNext,
    Long totalCount,
    String sortBy,
    String sortDirection

) {

}
