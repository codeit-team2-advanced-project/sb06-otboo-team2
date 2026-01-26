package codeit.sb06.otboo.comments.dto;

import java.util.List;

public record CommentDtoCursorResponse(
    List<CommentDto> data,
    String nextCursor,
    String nextIdAfter,
    boolean hasNext,
    Long totalCount,
    String sortBy,
    String sortDirection


) {

}
