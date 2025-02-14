package ru.practicum.controllers.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventPrivateClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.ParticipationRequestDto;
import ru.practicum.services.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController implements EventPrivateClient {

    private final EventService eventService;

    @Autowired
    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    public EventFullDto addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto event) {
        log.info("Add event {}", event);
        return eventService.addEvent(userId, event);
    }

    public List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(defaultValue = "0") Integer from) {
        return eventService.getPrivateEvents(userId, PageRequest.of(from, size));
    }

    public EventFullDto getEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return eventService.getPrivateEvent(userId, eventId);
    }

    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                             @PathVariable("eventId") Long eventId,
                             @Valid @RequestBody UpdateEventUserRequest event) {
        return eventService.updatePrivateEvent(userId, eventId, event);
    }

    public List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return eventService.getPrivateRequests(userId, eventId);
    }

    public EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable("userId") Long userId,
                                                                    @PathVariable("eventId") Long eventId,
                                                                    @RequestBody(required = false) EventRequestStatusUpdateRequest request) {
        return eventService.updateEventRequestStatus(userId, eventId, request);
    }
}
