package codeit.sb06.otboo.clothes.dto;

import java.util.List;
import java.util.UUID;

public record ClothesAttributeWithDefDto(
        UUID definitionId,
        String definitionName,
        List<String> selectableValues,
        String value
) {
    public static ClothesAttributeWithDefDto from(
            codeit.sb06.otboo.clothes.entity.ClothesAttribute attribute
    ) {
        return new ClothesAttributeWithDefDto(
                attribute.getDefinition().getId(),
                attribute.getDefinition().getName(),
                attribute.getDefinition().getValues().stream()
                        .map(codeit.sb06.otboo.clothes.entity.ClothesAttributeDefValue::getValue)
                        .toList(),
                attribute.getValue()
        );
    }
}
