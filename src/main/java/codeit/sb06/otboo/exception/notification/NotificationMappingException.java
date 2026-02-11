package codeit.sb06.otboo.exception.notification;

public class NotificationMappingException extends NotificationException {

    public static final String DEFAULT_MESSAGE = "알림 매핑 중 오류가 발생했습니다.";

    public NotificationMappingException() {
        super(DEFAULT_MESSAGE, 500);
    }
}
