package codeit.sb06.otboo.message.dto.response;

import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.enums.SortDirection;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DirectMessageDtoCursorResponse(
        List<DirectMessage> data,
        LocalDateTime nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        String sortBy,
        SortDirection sortDirection
) {
}
