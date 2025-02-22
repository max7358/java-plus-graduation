package ru.practicum.controllers;

import collector.UserAction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventClient;
import ru.practicum.dto.event.EventFilterDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventSort;
import ru.practicum.services.EventService;
import ru.practicum.services.RecommendationService;
import ru.practicum.stats.CollectorClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventController implements EventClient {
    private final EventService eventService;
    private final CollectorClient collectorClient;
    private final RecommendationService recommendationService;

    @GetMapping
    public Collection<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(required = false) Boolean onlyAvailable,
                                               @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        return eventService.getEvents(new EventFilterDto(text, categories, paid, rangeStart, rangeEnd, onlyAvailable),
                sort,
                from,
                size
        );
    }

    @GetMapping("/{id}")
    public EventFullDto find(@PathVariable Long id, @RequestHeader("X-EWM-USER-ID") long userId, HttpServletRequest request) {
        EventFullDto event = eventService.find(id);
        collectorClient.sendUserAction(userId, id, UserAction.ActionTypeProto.ACTION_VIEW);
        return event;
    }

    @GetMapping("/recommendations")
    public List<EventRecommendationDto> getRecommendations(
            @RequestHeader("X-EWM-USER-ID") long userId, @RequestParam(name = "size", defaultValue = "10") int size) {
        return recommendationService.getRecommendations(userId, size);
    }

    @PutMapping("/{eventId}/like")
    public void addLike(@PathVariable Long eventId,
                          @RequestHeader("X-EWM-USER-ID") long userId) {
        collectorClient.sendUserAction(userId, eventId, UserAction.ActionTypeProto.ACTION_LIKE);
    }
}
