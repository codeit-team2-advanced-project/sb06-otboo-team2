package codeit.sb06.otboo.exception.clothes;

public class InvalidClothesTypeException extends ClothesException{

    private static final String DEFAULT_MESSAGE = "type 값이 올바르지 않습니다.";

    public InvalidClothesTypeException(String type) {
        super(DEFAULT_MESSAGE + " type=" + type, 400);
    }

    public InvalidClothesTypeException(String type, Throwable cause) {
        super(DEFAULT_MESSAGE + " type=" + type, cause, 400);
    }
}
