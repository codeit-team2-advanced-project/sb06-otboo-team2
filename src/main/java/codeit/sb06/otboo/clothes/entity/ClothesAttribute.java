package codeit.sb06.otboo.clothes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "clothes_attribute",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_clothes_attribute_clothes_def",
                        columnNames = {"clothes_id", "definition_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClothesAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false)
    private ClothesAttributeDef definition;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ClothesAttribute(Clothes clothes,
                            ClothesAttributeDef definition,
                            String value) {
        this.clothes = clothes;
        this.definition = definition;
        this.value = value;

        clothes.addAttributeInternal(this);
    }

    public void changeValue(String value) {
        this.value = value;
    }
}
