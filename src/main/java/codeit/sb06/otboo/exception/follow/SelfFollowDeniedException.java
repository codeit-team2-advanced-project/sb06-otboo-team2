package codeit.sb06.otboo.exception.follow;

public class SelfFollowDeniedException extends FollowException {

  private static final String DEFAULT_MESSAGE = "자기 자신을 팔로우 할 수 없습니다.";

  public SelfFollowDeniedException() {
    super(DEFAULT_MESSAGE,400);
  }

  public SelfFollowDeniedException(Throwable cause) {
    super(DEFAULT_MESSAGE,cause,400);
  }
}
