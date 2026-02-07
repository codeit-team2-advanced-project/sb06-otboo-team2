package codeit.sb06.otboo.exception.clothes;

import java.util.List;
import java.util.UUID;

public class ClothesNotFoundException extends ClothesException {

    public ClothesNotFoundException(List<UUID> ids) {
        super("Clothes not found", 404);
        addDetail("ids", ids);
    }
}
