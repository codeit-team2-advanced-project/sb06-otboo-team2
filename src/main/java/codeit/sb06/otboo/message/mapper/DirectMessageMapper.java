package codeit.sb06.otboo.message.mapper;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.response.DirectMessageDtoCursorResponse;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.enums.SortDirection;
import codeit.sb06.otboo.user.dto.UserSummary;
import codeit.sb06.otboo.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class DirectMessageMapper {

    public static final String CURSOR_SORT_BY = "createdAt";
    public static final SortDirection CURSOR_SORT_DIRECTION = SortDirection.DESCENDING;

    public DirectMessageDto toDto(DirectMessage dm, User receiver) {

        UserSummary senderSummary = UserSummary.builder()
                .userId(dm.getId())
                .name(dm.getSender().getName())
//                .profileImageUrl(dm.getSender().getProfileImageUrl())
                .build();

        UserSummary receiverSummary = UserSummary.builder()
                .userId(receiver.getId())
                .name(receiver.getName())
//                .profileImageUrl(receiver.getProfileImageUrl())
                .build();

        return DirectMessageDto.builder()
                .id(dm.getId())
                .createdAt(dm.getCreatedAt())
                .sender(senderSummary)
                .receiver(receiverSummary)
                .content(dm.getContent())
                .build();
    }

    public DirectMessageDtoCursorResponse toDtoCursorResponse(List<DirectMessage> directMessages, int limit, long totalCount) {

        boolean hasNext = directMessages.size() > limit;
        LocalDateTime nextCursor = null;
        UUID nextIdAfter = null;
        if (hasNext) {
            directMessages = directMessages.subList(0, limit);
            // -1을 하는 이유는 인덱스가 0부터 시작하기 때문
            nextCursor = directMessages.get(limit - 1).getCreatedAt();
            nextIdAfter = directMessages.get(limit - 1).getId();
        }

        return DirectMessageDtoCursorResponse.builder()
                .data(directMessages)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .hasNext(hasNext)
                .totalCount(totalCount)
                .sortBy(CURSOR_SORT_BY)
                .sortDirection(CURSOR_SORT_DIRECTION)
                .build();
    }
}
