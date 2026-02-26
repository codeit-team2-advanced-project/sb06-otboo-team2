package codeit.sb06.otboo.exception.clothes;

import java.util.UUID;

public class ClothesForbiddenException extends ClothesException {
    private static final String DEFAULT_MESSAGE = "해당 리소스에 대한 권한이 없습니다.";

    public ClothesForbiddenException(UUID resourceId) {
        super(DEFAULT_MESSAGE + " resourceId=" + resourceId, 403);
    }

    public ClothesForbiddenException(UUID resourceId, UUID ownerId) {
        super(DEFAULT_MESSAGE + " resourceId=" + resourceId + ", ownerId=" + ownerId, 403);
    }
}
