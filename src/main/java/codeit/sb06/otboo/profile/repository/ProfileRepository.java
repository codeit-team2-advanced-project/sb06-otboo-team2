package codeit.sb06.otboo.profile.repository;

import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(User user);

}
