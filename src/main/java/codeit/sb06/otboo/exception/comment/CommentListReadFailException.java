package codeit.sb06.otboo.exception.comment;

public class CommentListReadFailException extends CommentException {

  private static final String DEFAULT_MESSAGE = "댓글 목록 조회 실패";

  public CommentListReadFailException() {
    super(DEFAULT_MESSAGE, 400);
  }

  public CommentListReadFailException(Throwable cause) {
    super(DEFAULT_MESSAGE, cause, 400);
  }
}
