package codeit.sb06.otboo.exception.user;

public class MailSendException extends UserException{

    private static final String DEFAULT_MESSAGE = "메일 전송에 실패했습니다.";

    public MailSendException() {
        super(DEFAULT_MESSAGE, 404);
    }

    public MailSendException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, 404);
    }
}
