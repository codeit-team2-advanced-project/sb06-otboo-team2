package codeit.sb06.otboo.message.service.impl;

import codeit.sb06.otboo.message.dto.DirectMessageDto;
import codeit.sb06.otboo.message.dto.request.DirectMessageCreateRequest;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.entity.DirectMessage;
import codeit.sb06.otboo.message.mapper.DirectMessageMapper;
import codeit.sb06.otboo.message.repository.DirectMessageRepository;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.message.service.ChatRoomService;
import codeit.sb06.otboo.message.service.DirectMessageService;
import codeit.sb06.otboo.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final DirectMessageMapper directMessageMapper;

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

        // 알림 이벤트 발행

        return directMessageMapper.toDto(saved, receiver);
    }
}
