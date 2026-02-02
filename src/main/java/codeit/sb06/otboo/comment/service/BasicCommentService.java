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
import java.util.List;
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

    feedRepository.findById(feedId)
        .orElseThrow();

    List<Comment> commentList = commentRepository.findCommentListByCursor(feedId, idAfter, limit);

    String nextCursor = null;
    UUID nextIdAfter = null;

    boolean hasNext = commentList.size() > limit;

    if(hasNext) {
      nextCursor = commentList.get(commentList.size() - 1).getId().toString();
      nextIdAfter = commentList.get(commentList.size() - 1).getId();
    }

    List<CommentDto> data = commentList.stream()
        .map(c -> CommentDto.of(c,AuthorDto.of(c.getUser())))
        .toList();

    long totalCount = commentRepository.countByFeedId(feedId);

    log.debug("");
    return new CommentDtoCursorResponse(
        data,
        nextCursor,
        nextIdAfter,
        hasNext,
        totalCount,
        "createdAt",
        "DESCENDING"
    );

  }
}