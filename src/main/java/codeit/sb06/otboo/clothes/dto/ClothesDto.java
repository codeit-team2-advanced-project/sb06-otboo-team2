package codeit.sb06.otboo.clothes.dto;

import codeit.sb06.otboo.clothes.entity.Clothes;

import java.util.List;
import java.util.UUID;

public record ClothesDto(
        UUID id,
        UUID ownerId,
        String name,
        String imageUrl,
        String type,
        List<ClothesAttributeWithDefDto> attributes
) {

    public static ClothesDto from(Clothes clothes) {
        return new ClothesDto(
                clothes.getId(),
                clothes.getOwnerId(),
                clothes.getName(),
                clothes.getImageUrl(),
                clothes.getType().name(),
                clothes.getAttributes().stream()
                        .map(attr -> new ClothesAttributeWithDefDto(
                                attr.getDefinition().getId(),
                                attr.getDefinition().getName(),
                                attr.getDefinition().getSelectableValues(),
                                attr.getValue()
                        ))
                        .toList()
        );
    }
}
