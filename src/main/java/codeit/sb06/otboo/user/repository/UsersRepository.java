package codeit.sb06.otboo.user.repository;

import codeit.sb06.otboo.user.entity.Users;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {

}
