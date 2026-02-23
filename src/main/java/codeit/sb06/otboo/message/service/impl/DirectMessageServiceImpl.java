package codeit.sb06.otboo.message.service.impl;

import codeit.sb06.otboo.exception.message.ChatRoomNotFoundException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.dto.response.DirectMessageDtoCursorResponse;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.mapper.DirectMessageMapper;
import codeit.sb06.otboo.message.repository.ChatRoomRepository;
import codeit.sb06.otboo.message.repository.DirectMessageRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.notification.publisher.NotificationEventPublisher;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.message.service.ChatRoomService;
import codeit.sb06.otboo.message.service.DirectMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final DirectMessageMapper directMessageMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationEventPublisher notificationEventPublisher;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public DirectMessageDto create(DirectMessageCreateRequest request) {

        User sender = userRepository.findById(request.senderId())
                .orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(UserNotFoundException::new);

        ChatRoom chatRoom = chatRoomService.getOrCreatePrivateRoom(request.senderId(), request.receiverId());

        DirectMessage dm = DirectMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(request.content())
                .build();

        DirectMessage saved = directMessageRepository.save(dm);

        log.info("DM 저장: {}", saved);

        notificationEventPublisher.publishDirectMessageCreatedEvent(
                receiver.getId(),
                sender.getName(),
                request.content());

        String dmKey = ChatRoom.generateDmKey(request.senderId(), request.receiverId());
        String destination = "/sub/direct-messages_" + dmKey;

        messagingTemplate.convertAndSend(destination, directMessageMapper.toDto(dm, receiver));

        return directMessageMapper.toDto(saved, receiver);
    }

    @Override
    public DirectMessageDtoCursorResponse getDirectMessages(UUID myUserId, UUID senderId, LocalDateTime cursor, UUID idAfter, int limit) {

        String dmKey = ChatRoom.generateDmKey(myUserId, senderId);

        ChatRoom chatRoom = chatRoomRepository.findByDmKey(dmKey)
                .orElseThrow(ChatRoomNotFoundException::new);

        User receiver = userRepository.findById(myUserId)
                .orElseThrow(UserNotFoundException::new);

        Slice<DirectMessage> directMessages;

        if(cursor == null && idAfter == null) {
            directMessages = directMessageRepository.findFirstPageByChatRoom(chatRoom, PageRequest.of(0, limit));
        } else {
            directMessages = directMessageRepository.findByChatRoomWithCursor(
                    chatRoom,
                    cursor,
                    idAfter,
                    // pageable로 원하는 개수만큼 조회
                    PageRequest.of(0, limit)
            );
        }

        return directMessageMapper.toDtoCursorResponse(directMessages, receiver);
    }
}
