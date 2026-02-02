package codeit.sb06.otboo.clothes.dto;


import jakarta.validation.constraints.NotBlank;

public record ClothesAttributeDto(
        @NotBlank(message = "definitionId는 필수입니다.")
        String definitionId,

        @NotBlank(message = "value는 필수입니다.")
        String value
) {
}
