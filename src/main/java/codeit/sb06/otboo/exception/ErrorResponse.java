package codeit.sb06.otboo.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private String exceptionName;
    private String message;
    private Map<String, String> details;

    public ErrorResponse(Exception exception,Map<String, String> details) {
        this.exceptionName = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.details = details;
    }

    public ErrorResponse(Exception exception) {
        this.exceptionName = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        details = new HashMap<>();
    }

}
