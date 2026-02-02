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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "selectable_values", nullable = false, columnDefinition = "jsonb")
    private List<String> selectableValues = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ClothesAttributeDef(String name, List<String> selectableValues) {
        this.name = name;
        if (selectableValues != null) {
            this.selectableValues = new ArrayList<>(selectableValues);
        }
    }

    //속성 수정시 사용하는 도메인 메서드
    public void replaceSelectableValues(List<String> newValues) {
        this.selectableValues.clear();
        if (newValues != null) {
            this.selectableValues.addAll(newValues);
        }
    }

    public void changeName(String name) {
        this.name = name;
    }
}
