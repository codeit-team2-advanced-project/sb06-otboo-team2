package codeit.sb06.otboo.message.mapper;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.user.dto.UserSummary;
import codeit.sb06.otboo.user.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class DirectMessageMapper {

    public DirectMessageDto toDto(DirectMessage dm, Users receiver) {

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
}
