package codeit.sb06.otboo.message.mapper;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.response.DirectMessageDtoCursorResponse;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.enums.SortDirection;
import codeit.sb06.otboo.user.dto.UserSummary;
import codeit.sb06.otboo.user.entity.User;
import org.springframework.data.domain.Slice;
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
                .profileImageUrl(dm.getSender().getProfileImageUrl())
                .build();

        UserSummary receiverSummary = UserSummary.builder()
                .userId(receiver.getId())
                .name(receiver.getName())
                .profileImageUrl(receiver.getProfileImageUrl())
                .build();

        return DirectMessageDto.builder()
                .id(dm.getId())
                .createdAt(dm.getCreatedAt())
                .sender(senderSummary)
                .receiver(receiverSummary)
                .content(dm.getContent())
                .build();
    }

    public DirectMessageDtoCursorResponse toDtoCursorResponse(Slice<DirectMessage> directMessages) {

        List<DirectMessage> content = directMessages.getContent();
        boolean hasNext = directMessages.hasNext();
        LocalDateTime nextCursor = null;
        UUID nextIdAfter = null;
        if (!content.isEmpty() && hasNext) {
            DirectMessage lastMessage = content.get(content.size() - 1);
            nextCursor = lastMessage.getCreatedAt();
            nextIdAfter = lastMessage.getId();
        }

        return DirectMessageDtoCursorResponse.builder()
                .data(directMessages.getContent())
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .hasNext(hasNext)
                .totalCount(null)
                .sortBy(CURSOR_SORT_BY)
                .sortDirection(CURSOR_SORT_DIRECTION)
                .build();
    }
}
