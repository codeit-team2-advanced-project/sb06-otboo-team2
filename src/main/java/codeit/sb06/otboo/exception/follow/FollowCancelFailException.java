package codeit.sb06.otboo.exception.follow;

public class FollowCancelFailException extends FollowException {

  private static final String DEFAULT_MESSAGE = "팔로우 취소 실패";

  public FollowCancelFailException() {super(DEFAULT_MESSAGE,400);}
  public FollowCancelFailException(Throwable cause) {super(DEFAULT_MESSAGE,cause,400);}
}
