package codeit.sb06.otboo.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Users {

    @Id
    private UUID id;
    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
