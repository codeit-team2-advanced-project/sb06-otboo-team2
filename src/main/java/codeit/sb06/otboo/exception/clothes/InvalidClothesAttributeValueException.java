package codeit.sb06.otboo.exception.clothes;

import java.util.UUID;

public class InvalidClothesAttributeValueException extends ClothesException {

  private static final String DEFAULT_MESSAGE = "허용되지 않은 속성 값입니다.";

  public InvalidClothesAttributeValueException(UUID definitionId, String value) {
    super(
            DEFAULT_MESSAGE + " definitionId=" + definitionId + ", value=" + value,
            400
    );
  }
}
