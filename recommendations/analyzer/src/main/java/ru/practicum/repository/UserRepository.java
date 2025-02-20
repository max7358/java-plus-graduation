package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.UserAction;

import java.util.List;

public interface UserRepository extends JpaRepository<UserAction, Long> {
    interface InteractionCount {
        Long getEventId();

        Double getTotalWeight();
    }

    List<UserAction> findByUserId(long userId);

    @Query("SELECT eventId, SUM(actionType) AS total " +
            "FROM UserAction " +
            "WHERE eventId IN :eventIds " +
            "GROUP BY eventId")
    List<InteractionCount> findInteractionCountsByEventIds(List<Long> eventIds);

    List<Long> findInteractionsByUserIdOrderByTimestamp(Long userId);
}
