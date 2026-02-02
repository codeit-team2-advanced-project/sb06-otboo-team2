package codeit.sb06.otboo.clothes.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ClothesAttributeDefDto(
        UUID id,
        String name,
        List<String> selectableValues,
        LocalDateTime createdAt
) {
}
