package codeit.sb06.otboo.clothes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ClothesCreateRequest(
        @NotBlank(message = "ownerId는 필수입니다.")
        String ownerId,

        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 100, message = "name은 100자 이하여야 합니다.")
        String name,

        @NotBlank(message = "type은 필수입니다.")
        String type,

        List<@Valid ClothesAttributeDto> attributes
) {
}
