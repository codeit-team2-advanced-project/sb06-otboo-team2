package codeit.sb06.otboo.profile.entity;

import codeit.sb06.otboo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private LocalDateTime birthday;
    private int sensitivity;
    private String imageUrl;

    private String gender;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> locations;

    private int followerCount;
    private int followingCount;

    @OneToOne
    private User userId;

    public static Profile from(User user) {
        return Profile.builder()
            .name(user.getName())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .birthday(LocalDateTime.MIN)
            .sensitivity(3)
            .gender("UNSPECIFIED")
            .imageUrl(user.getProfileImageUrl())
            .locations(List.of())
            .userId(user)
            .followerCount(0)
            .followingCount(0)
            .build();
    }
}
