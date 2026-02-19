package codeit.sb06.otboo.feed.search.service;

import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.search.document.FeedDocument;
import codeit.sb06.otboo.feed.search.dto.FeedSearchHit;
import codeit.sb06.otboo.feed.search.repository.FeedDocumentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedSearchService {

  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;

  private final FeedDocumentRepository feedDocumentRepository;

  public List<FeedSearchHit> search(String keyword, UUID authorId, Integer size) {
    PageRequest pageable = PageRequest.of(
        0,
        normalizeSize(size),
        Sort.by(Sort.Direction.DESC, "createdAt")
    );

    String trimmedKeyword = keyword == null ? null : keyword.trim();
    String authorIdText = authorId == null ? null : authorId.toString();

    List<FeedDocument> documents;
    if (hasText(trimmedKeyword) && hasText(authorIdText)) {
      documents = feedDocumentRepository.findByContentContainingAndAuthorId(
          trimmedKeyword,
          authorIdText,
          pageable
      );
    } else if (hasText(trimmedKeyword)) {
      documents = feedDocumentRepository.findByContentContaining(trimmedKeyword, pageable);
    } else if (hasText(authorIdText)) {
      documents = feedDocumentRepository.findByAuthorId(authorIdText, pageable);
    } else {
      documents = feedDocumentRepository.findAllBy(pageable);
    }

    return documents.stream()
        .map(FeedSearchHit::from)
        .toList();
  }

  public void index(Feed feed) {
    try {
      feedDocumentRepository.save(FeedDocument.from(feed));
    } catch (Exception e) {
      log.warn("Failed to index feed document. feedId={}", feed.getId(), e);
    }
  }

  public void delete(UUID feedId) {
    try {
      feedDocumentRepository.deleteById(feedId.toString());
    } catch (Exception e) {
      log.warn("Failed to delete feed document. feedId={}", feedId, e);
    }
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private int normalizeSize(Integer size) {
    if (size == null) {
      return DEFAULT_SIZE;
    }
    if (size < 1) {
      return 1;
    }
    return Math.min(size, MAX_SIZE);
  }
}
