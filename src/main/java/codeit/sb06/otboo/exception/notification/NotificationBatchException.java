package codeit.sb06.otboo.exception.notification;

public class NotificationBatchException extends NotificationException {
    public static final String DEFAULT_MESSAGE = "알림 배치 처리 중 오류가 발생했습니다.";

    public NotificationBatchException() {
        super(DEFAULT_MESSAGE, 500);
    }

    public NotificationBatchException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 404);
    }
}
