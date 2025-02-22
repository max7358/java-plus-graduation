package ru.practicum.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.service.EventService;

@Component
public class EventConsumer {

    private final EventService eventService;

    public EventConsumer(EventService eventService) {
        this.eventService = eventService;
    }

    @KafkaListener(topics = "${analyzer.kafka.topic.event}", containerFactory = "eventKafkaListenerContainerFactory")
    public void consumeEventSimilarity(EventSimilarityAvro event) {
        eventService.saveEventSimilarity(event);
    }
}
