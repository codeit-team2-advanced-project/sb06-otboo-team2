package codeit.sb06.otboo.clothes.dto;

public record ClothesAttributeDefCreateRequest(
        @NotBlank(message = "속성 이름은 필수값입니다.")
        @Size(max = 100, message = "속성 이름은 최대 100자까지 가능합니다.")
        String name,

        @NotEmpty(message = "선택 가능한 값은 최소 1개 이상이어야 합니다.")
        List<
                @NotBlank(message = "선택 가능한 값은 공백일 수 없습니다.")
                @Size(max = 100, message = "선택 가능한 값은 최대 100자까지 가능합니다.")
                        String
                > selectableValues
) {
}
