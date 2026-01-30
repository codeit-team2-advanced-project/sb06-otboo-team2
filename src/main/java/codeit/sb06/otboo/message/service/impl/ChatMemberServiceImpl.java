package codeit.sb06.otboo.message.service.impl;

import codeit.sb06.otboo.message.entity.ChatMember;
import codeit.sb06.otboo.message.entity.ChatRoom;
import codeit.sb06.otboo.message.repository.ChatMemberRepository;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.message.service.ChatMemberService;
import codeit.sb06.otboo.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMemberServiceImpl implements ChatMemberService {

    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;

    @Override
    public ChatMember create(ChatRoom chatRoom, UUID userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        ChatMember chatMember = ChatMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        return chatMemberRepository.save(chatMember);
    }
}
