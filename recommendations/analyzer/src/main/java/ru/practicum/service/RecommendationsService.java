package ru.practicum.service;

import analyzer.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationsService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<Recommendation.RecommendedEventProto> getRecommendationsForUser(long userId, long maxResults) {
        List<UserAction> actions = userRepository.findByUserId(userId);
        if (actions.isEmpty()) {
            return List.of();
        }

        List<Long> recentInteractions = userRepository.findInteractionsByUserIdOrderByTimestamp(userId);
        Set<EventRepository.SimilarEvent> similarEvents = new HashSet<>(
                eventRepository.selectSimilarEventsExcludingUserInteractions(recentInteractions,
                        userId));

        return similarEvents.stream()
                .sorted(Comparator.comparingDouble(EventRepository.SimilarEvent::getSimilarityScore).reversed())
                .limit(maxResults)
                .map(entry -> Recommendation.RecommendedEventProto.newBuilder()
                        .setEventId(entry.getSimilarEventId())
                        .setScore(entry.getSimilarityScore())
                        .build())
                .toList();
    }

    public List<Recommendation.RecommendedEventProto> getSimilarEvents(long eventId, long userId, long maxResults) {
        List<EventRepository.SimilarEvent> similarEventResults =
                eventRepository.findSimilarEventsExcludingUserInteractions(eventId, userId);

        return similarEventResults.stream()
                .limit(maxResults)
                .map(result -> Recommendation.RecommendedEventProto.newBuilder()
                        .setEventId(result.getSimilarEventId())
                        .setScore(result.getSimilarityScore().floatValue())
                        .build())
                .toList();
    }

    public List<Recommendation.RecommendedEventProto> getInteractionCounts(List<Long> eventIds) {
        List<UserRepository.InteractionCount> interactionCounts =
                userRepository.findInteractionCountsByEventIds(eventIds);

        return interactionCounts.stream()
                .map(result -> Recommendation.RecommendedEventProto.newBuilder()
                        .setEventId(result.getEventId())
                        .setScore(result.getTotalWeight().floatValue())
                        .build())
                .toList();
    }
}
