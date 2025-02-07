package ewm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ewm.enums.EventCommentStatus;
import ewm.models.EventComment;

import java.util.List;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {

    List<EventComment> findAllByAuthorId(Long authorID, Pageable pageable);

    List<EventComment> findAllByEventId(Long eventId, Pageable pageable);

    List<EventComment> findAllByAuthorIdAndEventId(Long authorID, Long eventId, Pageable pageable);

    List<EventComment> findAllByEventIdAndStatus(Long eventId, EventCommentStatus status, Pageable pageable);

}
