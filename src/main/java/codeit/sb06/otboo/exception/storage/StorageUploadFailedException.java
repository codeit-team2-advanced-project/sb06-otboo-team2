package codeit.sb06.otboo.exception.storage;

import org.springframework.http.HttpStatus;

public class StorageUploadFailedException extends StorageException {

  private static final String DEFAULT_MESSAGE = "파일 업로드에 실패했습니다.";

  public StorageUploadFailedException(String key, Throwable cause) {
    super(DEFAULT_MESSAGE, cause, HttpStatus.INTERNAL_SERVER_ERROR.value());
    addDetail("key", key);
  }

  public StorageUploadFailedException(String key, String reason, Throwable cause) {
    super(DEFAULT_MESSAGE, cause, HttpStatus.INTERNAL_SERVER_ERROR.value());
    addDetail("key", key);
    addDetail("reason", reason);
  }
}
