package codeit.sb06.otboo.profile.entity;

import codeit.sb06.otboo.profile.dto.ProfileUpdateRequest;
import codeit.sb06.otboo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String birthday;
    private int sensitivity;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private Long followerCount;
    private Long followingCount;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    public static Profile from(User user) {
        return Profile.builder()
            .name(user.getName())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .birthday(null)
            .sensitivity(3)
            .gender(null)
            .imageUrl(null)
            .userId(user)
            .followerCount(0L)
            .followingCount(0L)
            .build();
    }

    public void updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        if(profileUpdateRequest.name() != null){
            this.name = profileUpdateRequest.name();
        }
        if(profileUpdateRequest.birthDate() != null){
            this.birthday = profileUpdateRequest.birthDate();
        }
        if(profileUpdateRequest.temperatureSensitivity() != 0){
            this.sensitivity = profileUpdateRequest.temperatureSensitivity();
        }
        if(profileUpdateRequest.gender() == null) {
            this.gender = null;
        } else {
            this.gender = Gender.valueOf(profileUpdateRequest.gender());
        }
    }

    public void changeProfileImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    /**
     * 팔로워, 팔로잉 수 증감 메서드 추가
     * */
    public void increaseFollowerCount() {
        this.followerCount++;
    }

    public void increaseFollowingCount() {
        this.followingCount++;
    }

    public void decreaseFollowerCount() {
        if(this.followerCount>0){
            this.followerCount--;
        }
    }

    public void decreaseFollowingCount() {
        if(this.followingCount>0){
            this.followingCount--;
        }
    }
}
