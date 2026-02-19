package codeit.sb06.otboo.feed.search.repository;

import codeit.sb06.otboo.feed.search.document.FeedDocument;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FeedDocumentRepository extends ElasticsearchRepository<FeedDocument, String> {

  List<FeedDocument> findByContentContaining(String keyword, Pageable pageable);

  List<FeedDocument> findByAuthorId(String authorId, Pageable pageable);

  List<FeedDocument> findByContentContainingAndAuthorId(String keyword, String authorId, Pageable pageable);

  List<FeedDocument> findAllBy(Pageable pageable);
}
