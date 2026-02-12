package codeit.sb06.otboo.feed.dto;

import codeit.sb06.otboo.clothes.dto.ClothesAttributeWithDefDto;
import codeit.sb06.otboo.clothes.entity.Clothes;
import java.util.List;
import java.util.UUID;

public record OotdDto(
    UUID clothesId,
    String name,
    String imageUrl,
    String type,
    List<ClothesAttributeWithDefDto> attributes
) {
    public static OotdDto from(Clothes clothes) {
        return new OotdDto(
                clothes.getId(),
                clothes.getName(),
                clothes.getImageUrl(),
                clothes.getType().name(),
                clothes.getAttributes().stream()
                        .map(ClothesAttributeWithDefDto::from)
                        .toList()
        );
    }
}
