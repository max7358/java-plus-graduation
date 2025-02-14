package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "event-service", contextId = "event-private", path = "/users/{userId}/events")
public interface EventPrivateClient {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto event);

    @GetMapping
    List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(defaultValue = "0") Integer from);

    @GetMapping("{eventId}")
    EventFullDto getEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId);

    @PatchMapping("{eventId}")
    EventFullDto updateEvent(@PathVariable("userId") Long userId,
                             @PathVariable("eventId") Long eventId,
                             @Valid @RequestBody UpdateEventUserRequest event);

    @GetMapping("{eventId}/requests")
    List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId);

    @PatchMapping("{eventId}/requests")
    EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable("userId") Long userId,
                                                             @PathVariable("eventId") Long eventId,
                                                             @RequestBody(required = false) EventRequestStatusUpdateRequest request);
}
