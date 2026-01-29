package codeit.sb06.otboo.message.dto.response;

import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.enums.SortDirection;

import java.util.List;
import java.util.UUID;

public record DirectMessageDtoCursorResponse(
        List<DirectMessage> data,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        String sortBy,
        SortDirection sortDirection
) {
}
