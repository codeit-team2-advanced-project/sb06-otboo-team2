package codeit.sb06.otboo.comment.service;

import codeit.sb06.otboo.comment.dto.CommentDtoCursorResponse;
import codeit.sb06.otboo.comment.repository.CommentRepository;
import codeit.sb06.otboo.comment.dto.AuthorDto;
import codeit.sb06.otboo.comment.dto.CommentCreateRequest;
import codeit.sb06.otboo.comment.dto.CommentDto;
import codeit.sb06.otboo.comment.entity.Comment;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BasicCommentService implements CommentService {

  private final CommentRepository commentRepository;
  private final FeedRepository feedRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public CommentDto createComment(UUID feedId, CommentCreateRequest commentCreateRequest) {

    Feed feed = feedRepository.findById(feedId)
        .orElseThrow();
    User author = userRepository.findById(commentCreateRequest.authorId())
        .orElseThrow();
    Comment comment = Comment.builder()
        .content(commentCreateRequest.content())
        .feed(feed)
        .user(author)
        .build()
        ;

    log.debug("댓글 생성 '{}'", feedId);

    return CommentDto.of(
        commentRepository.save(comment),
        AuthorDto.of(author)
    );
  }

  @Override
  public CommentDtoCursorResponse getComments(UUID feedId, String cursor, UUID idAfter,
      Integer limit) {
    return null;
  }
}