package codeit.sb06.otboo.follows.dto;

import java.util.List;

public record FollowListResponse(
    List<FollowDto> data,
    String nextCursor,
    String nextIdAfter,
    boolean hasNext,
    Long totalCount,
    String sortBy,
    String sortDirection

) {

}
