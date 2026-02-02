package codeit.sb06.otboo.notification.dto.response;

import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.SortDirection;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record NotificationDtoCursorResponse (
        List<Notification> data,
        LocalDateTime nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        Long totalCount,
        String sortBy,
        SortDirection sortDirection
){
}
