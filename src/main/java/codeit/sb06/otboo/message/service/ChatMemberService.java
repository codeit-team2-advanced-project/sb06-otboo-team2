package codeit.sb06.otboo.message.service;

import codeit.sb06.otboo.message.entity.ChatMember;
import codeit.sb06.otboo.message.entity.ChatRoom;

import java.util.UUID;

public interface ChatMemberService {

    ChatMember create(ChatRoom chatRoom, UUID userId);
}
