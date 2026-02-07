package codeit.sb06.otboo.notification.repository;

import codeit.sb06.otboo.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("""
            SELECT noti FROM Notification noti
            WHERE noti.receiverId = :myUserId
            AND (
                noti.createdAt < :cursor
                OR (noti.createdAt = :cursor
                    AND (
                         :idAfter IS NULL
                         OR noti.id < :idAfter
                    )
                 )
            )
            ORDER BY noti.createdAt DESC, noti.id DESC
            """)
    Slice<Notification> findByMyUserIdWithCursor(LocalDateTime cursor, UUID idAfter, UUID myUserId, Pageable pageable);

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
