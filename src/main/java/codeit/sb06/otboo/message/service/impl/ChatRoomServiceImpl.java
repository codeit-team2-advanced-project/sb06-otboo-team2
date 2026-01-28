package codeit.sb06.otboo.message.service.impl;

import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.repository.ChatRoomRepository;
import codeit.sb06.otboo.message.service.ChatMemberService;
import codeit.sb06.otboo.message.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberService chatMemberService;

    public ChatRoom getOrCreatePrivateRoom(UUID senderId, UUID receiverId) {

        String dmKey = ChatRoom.generateDmKey(senderId, receiverId);

        return chatRoomRepository.findByDmKey(dmKey)
                .orElseGet(() -> {
                    ChatRoom newRoom = chatRoomRepository.save(new ChatRoom(dmKey));
                    newRoom.addChatMember(chatMemberService.create(newRoom, senderId));
                    newRoom.addChatMember(chatMemberService.create(newRoom, receiverId));
                    return newRoom;
                });
    }
}
