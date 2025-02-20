package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.mapper.EventSimilarityMapper;
import ru.practicum.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void saveEventSimilarity(EventSimilarityAvro event) {
        eventRepository.save(EventSimilarityMapper.toModel(event));
    }
}
