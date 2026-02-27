package codeit.sb06.otboo.exception.comment;

public class CommentCreateFailException extends CommentException {

  private static final String DEFAULT_MESSAGE = "댓글 등록 실패";

  public CommentCreateFailException() {
    super(DEFAULT_MESSAGE, 400);
  }

  public CommentCreateFailException(Throwable cause) {
    super(DEFAULT_MESSAGE, cause, 400);
  }
}