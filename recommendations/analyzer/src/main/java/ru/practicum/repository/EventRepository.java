package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EventSimilarity;

import java.util.List;

public interface EventRepository extends JpaRepository<EventSimilarity, Long> {
    interface SimilarEvent {
        Long getSimilarEventId();

        Double getSimilarityScore();
    }

    @Query("SELECT eventA, score  " +
            "FROM EventSimilarity " +
            "WHERE eventA = :eventId AND eventB NOT IN (SELECT eventId FROM UserAction WHERE userId = :userId) " +
            "ORDER BY score DESC")
    List<SimilarEvent> findSimilarEventsExcludingUserInteractions(Long eventId, Long userId);

    @Query("SELECT eventB, score " +
            "FROM EventSimilarity " +
            "WHERE eventA = :eventId AND score > 0 " +
            "ORDER BY score DESC")
    List<SimilarEvent> findSimilarEventsForEvent(Long eventId);

    @Query("SELECT s FROM EventSimilarity s " +
            "WHERE s.eventA IN :eventIds " +
            "AND s.eventB NOT IN (SELECT eventId FROM UserAction WHERE userId = :userId)")
    List<SimilarEvent> selectSimilarEventsExcludingUserInteractions(List<Long> eventIds, Long userId);
}
