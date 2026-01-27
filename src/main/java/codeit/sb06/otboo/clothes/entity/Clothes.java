package codeit.sb06.otboo.clothes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clothes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "clothes_type")
    private ClothesType type;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "clothes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClothesAttribute> attributes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Clothes(UUID ownerId, String name, String imageUrl, ClothesType type) {
        this.ownerId = ownerId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    protected void addAttributeInternal(ClothesAttribute attribute) {
        this.attributes.add(attribute);
    }
}
