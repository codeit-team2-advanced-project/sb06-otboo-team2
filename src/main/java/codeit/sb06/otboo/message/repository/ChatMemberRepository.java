package codeit.sb06.otboo.message.repository;

import codeit.sb06.otboo.message.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatMemberRepository extends JpaRepository<ChatMember, UUID> {
}
