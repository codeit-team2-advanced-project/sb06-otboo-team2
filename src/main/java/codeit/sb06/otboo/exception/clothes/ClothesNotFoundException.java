package codeit.sb06.otboo.exception.clothes;

import java.util.List;
import java.util.UUID;


public class ClothesNotFoundException extends ClothesException {

    private static final String DEFAULT_MESSAGE = "존재하지 않는 의상입니다.";

    public ClothesNotFoundException(List<UUID> ids) {
        super("Clothes not found", 404);
        addDetail("ids", ids);
    }

    public ClothesNotFoundException(UUID clothesId) {
        super(DEFAULT_MESSAGE + " clothesId=" + clothesId, 404);
        addDetail("clothesId", clothesId);
    }
}
