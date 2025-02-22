package ru.practicum.services;

import analyzer.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.stats.AnalyzerClient;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    AnalyzerClient analyzerClient;

    public List<EventRecommendationDto> getRecommendations(long userId, int size) {
        Stream<Recommendation.RecommendedEventProto> recommendationsForUser = analyzerClient.getRecommendationsForUser(userId, size);

        return recommendationsForUser.map(recommendedEventProto ->
                        new EventRecommendationDto(recommendedEventProto.getEventId(), recommendedEventProto.getScore()))
                .toList();
    }
}
