package codeit.sb06.otboo.clothes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clothes_attribute_def")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class ClothesAttributeDef {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 외부 수정 불가능하게 getter 차단
    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "definition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClothesAttributeDefValue> values = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ClothesAttributeDef(String name) {
        this.name = name;
    }

    //속성 수정시 사용하는 도메인 메서드
    public void replaceValues(List<String> newValues) {
        this.values.clear();
        if (newValues == null) {
            return;
        }
        for (String v : newValues) {
            // 생성자에서 연관관계 완성 + 리스트에 자동 추가
            new ClothesAttributeDefValue(this, v);
        }
    }

    public void changeName(String name) {
        this.name = name;
    }

    public List<ClothesAttributeDefValue> getValues() {
        return List.copyOf(values);
    }

    protected void addValueInternal(ClothesAttributeDefValue value) {
        this.values.add(value);
    }
}
