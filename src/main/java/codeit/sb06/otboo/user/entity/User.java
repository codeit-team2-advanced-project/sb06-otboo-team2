package codeit.sb06.otboo.user.entity;

import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String profileImageUrl;
    private String password;

    public static User from(UserCreateRequest userCreateRequest) {
        return User.builder()
            .email(userCreateRequest.email())
            .role(Role.USER)
            .isLocked(false)
            .name(userCreateRequest.name())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .profileImageUrl(null)
            .password(null)
            .build();
    }

    public void setEncryptPassword(PasswordEncoder passwordEncoder, String rawPassword) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void changeLockStatus(boolean isLocked) {
        this.isLocked = isLocked;
    }
}
