package codeit.sb06.otboo.clothes.dto;

import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDefValue;

import java.util.List;
import java.util.UUID;

public record RecommendedClothesDto(
        UUID clothesId,
        UUID ownerId,
        String name,
        String imageUrl,
        String type,
        List<ClothesAttributeWithDefDto> attributes
) {
    public static RecommendedClothesDto from(Clothes clothes) {
        return new RecommendedClothesDto(
                clothes.getId(),
                clothes.getOwnerId(),
                clothes.getName(),
                clothes.getImageUrl(),
                clothes.getType().name(),
                clothes.getAttributes().stream()
                        .map(attr -> new ClothesAttributeWithDefDto(
                                attr.getDefinition().getId(),
                                attr.getDefinition().getName(),
                                attr.getDefinition().getValues().stream()
                                        .map(ClothesAttributeDefValue::getValue)
                                        .toList(),
                                attr.getValue()
                        ))
                        .toList()
        );
    }
}
