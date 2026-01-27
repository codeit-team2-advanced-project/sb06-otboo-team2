package codeit.sb06.otboo.clothes.dto;

import java.util.List;

public record ClothesCreateRequest(
        String ownerId,
        String name,
        String type,
        List<ClothesAttributeDto> attributes
) {
}
