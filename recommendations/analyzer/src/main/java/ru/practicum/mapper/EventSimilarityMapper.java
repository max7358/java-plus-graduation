package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

@UtilityClass
public class EventSimilarityMapper {
    public EventSimilarity toModel(EventSimilarityAvro avro) {
        return EventSimilarity.builder()
                .eventA(avro.getEventIdA())
                .eventB(avro.getEventIdB())
                .score(avro.getScore())
                .timestamp(avro.getTimestamp())
                .build();
    }
}
