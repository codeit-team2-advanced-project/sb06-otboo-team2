package codeit.sb06.otboo.exception.clothes;

import java.util.UUID;

public class ClothesAttributeDefNotFoundException extends ClothesException {
    private static final String DEFAULT_MESSAGE = "존재하지 않는 속성 정의입니다.";

    public ClothesAttributeDefNotFoundException(UUID definitionId) {
        super(DEFAULT_MESSAGE + " definitionId=" + definitionId, 400);
    }
}
