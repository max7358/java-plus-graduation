package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.EventValue;
import ru.practicum.model.SimilarityValue;

import java.time.Instant;
import java.util.*;

@Service
public class AggregationService {

    private final Map<Long, Map<Long, Double>> userActionMap = new HashMap<>();
    private final Map<Long, EventValue> eventStatMap = new HashMap<>();
    private final Map<Long, Map<Long, SimilarityValue>> eventSimilarityMap = new HashMap<>();

    public Optional<List<EventSimilarityAvro>> updateState(UserActionAvro userActionAvro) {
        Long userId = userActionAvro.getUserId();
        long eventId = userActionAvro.getEventId();
        double weight = convert(userActionAvro.getActionType());
        List<EventSimilarityAvro> eventSimilarityAvroList = new ArrayList<>();

        if (userActionMap.isEmpty()) {
            userActionMap.put(userId, new HashMap<>(Map.of(eventId, weight)));
            return Optional.empty();
        }

        EventValue eventStat = eventStatMap.computeIfAbsent(eventId, k -> new EventValue());
        double current = userActionMap.getOrDefault(userId, Collections.emptyMap()).getOrDefault(eventId, 0.0);

        if (weight > current) {
            double delta = weight - current;
            userActionMap.put(userId, new HashMap<>(Map.of(eventId, weight)));
            eventStat.setEventWeightSum(eventStat.getEventWeightSum() + delta);
            eventStat.updateSumSquareWeight();
            eventStatMap.put(eventId, eventStat);

            Map<Long, Double> val = userActionMap.get(userId);
            if (val.size() > 1) {
                val.keySet().stream()
                        .filter(l -> !l.equals(eventId) && weight < val.get(l))
                        .forEach(l -> calculateSimilarity(eventId, l, delta, eventStat, eventSimilarityAvroList));
            }

            if (!eventSimilarityMap.isEmpty()) {
                return Optional.of(eventSimilarityAvroList);
            }
        }
        return Optional.empty();
    }

    private double convert(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 1;
            case REGISTER -> 2;
            case LIKE -> 3;
            default -> 0.0;
        };
    }

    private void calculateSimilarity(long eventId, Long l, double delta, EventValue eventStat, List<EventSimilarityAvro> eventSimilarityAvroList) {
        long eventIdA = Math.min(eventId, l);
        long eventIdB = Math.max(eventId, l);
        SimilarityValue similarityStat = eventSimilarityMap
                .computeIfAbsent(eventIdA, k -> new HashMap<>())
                .computeIfAbsent(eventIdB, k -> new SimilarityValue());
        similarityStat.setEventMinSum(similarityStat.getEventMinSum() + delta);
        similarityStat.updateSimilarity(eventStat.getEventWeightSumSquare(), eventStatMap.get(l).getEventWeightSumSquare());
        eventSimilarityAvroList.add(EventSimilarityAvro.newBuilder()
                .setEventIdA(eventIdA)
                .setEventIdB(eventIdB)
                .setScore(similarityStat.getSimilarity())
                .setTimestamp(Instant.now())
                .build());
    }
}

