package codeit.sb06.otboo.notification.mapper;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.dto.response.NotificationDtoCursorResponse;
import codeit.sb06.otboo.notification.entity.Notification;
import codeit.sb06.otboo.notification.enums.SortDirection;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationMapper {

    public static final String CURSOR_SORT_BY = "createdAt";
    public static final SortDirection CURSOR_SORT_DIRECTION = SortDirection.DESCENDING;

    public NotificationDto toDto(Notification notification) {

        return NotificationDto.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .receiverId(notification.getReceiverId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .level(notification.getLevel())
                .build();
    }

    public NotificationDtoCursorResponse toDtoCursorResponse(Slice<Notification> notifications) {

        List<Notification> content = notifications.getContent();
        boolean hasNext = notifications.hasNext();
        LocalDateTime nextCursor = null;
        UUID nextIdAfter = null;

        if(!content.isEmpty() && hasNext) {
            Notification last = content.get(content.size() - 1);
            nextCursor = last.getCreatedAt();
            nextIdAfter = last.getId();
        }

        return NotificationDtoCursorResponse.builder()
                .data(content)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .hasNext(hasNext)
                .totalCount(null)
                .sortBy(CURSOR_SORT_BY)
                .sortDirection(CURSOR_SORT_DIRECTION)
                .build();
    }
}
