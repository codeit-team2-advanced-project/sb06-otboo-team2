package codeit.sb06.otboo.message.dto.response;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.enums.SortDirection;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DirectMessageDtoCursorResponse(
        List<DirectMessageDto> data,
        LocalDateTime nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        Long totalCount,
        String sortBy,
        SortDirection sortDirection
) {
}
