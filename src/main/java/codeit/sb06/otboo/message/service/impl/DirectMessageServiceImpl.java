package codeit.sb06.otboo.message.service.impl;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.dto.response.DirectMessageDtoCursorResponse;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.enums.SortDirection;
import codeit.sb06.otboo.message.mapper.DirectMessageMapper;
import codeit.sb06.otboo.message.repository.ChatRoomRepository;
import codeit.sb06.otboo.message.repository.DirectMessageRepository;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.message.service.ChatRoomService;
import codeit.sb06.otboo.message.service.DirectMessageService;
import codeit.sb06.otboo.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public DirectMessageDto create(DirectMessageCreateRequest request) {

        Users sender = userRepository.findById(request.senderId())
                .orElseThrow(()-> new IllegalArgumentException("Sender not found"));
        Users receiver = userRepository.findById(request.receiverId())
                .orElseThrow(()-> new IllegalArgumentException("Receiver not found"));

        ChatRoom chatRoom = chatRoomService.getOrCreatePrivateRoom(request.senderId(), request.receiverId());

        DirectMessage dm = DirectMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(request.content())
                .build();

        DirectMessage saved = directMessageRepository.save(dm);

        log.info("DM 저장: {}", saved);

        // 알림 이벤트 발행

        return directMessageMapper.toDto(saved, receiver);
    }

    @Override
    public DirectMessageDtoCursorResponse getDirectMessages(UUID myUserId, UUID senderId, LocalDateTime cursor, UUID idAfter, int limit) {

        String dmKey = ChatRoom.generateDmKey(myUserId, senderId);

        ChatRoom chatRoom = chatRoomRepository.findByDmKey(dmKey)
                .orElseThrow(()-> new IllegalArgumentException("ChatRoom not found for dmKey: " + dmKey));

        List<DirectMessage> directMessages = directMessageRepository.findByChatRoomWithCursor(
                chatRoom,
                cursor,
                idAfter,
                // 다음 페이지 존재 여부 확인을 위해 limit + 1개 조회
                // pageable로 원하는 개수만큼 조회
                PageRequest.of(0, limit + 1)
        );

        long totalCount = directMessageRepository.countByChatRoom(chatRoom);

        return directMessageMapper.toDtoCursorResponse(directMessages, limit, totalCount);
    }
}
