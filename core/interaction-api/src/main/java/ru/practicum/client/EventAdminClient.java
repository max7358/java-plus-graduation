package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "event-service", contextId = "event-admin", path = "/admin/events")
public interface EventAdminClient {
    @GetMapping
    List<EventFullDto> getEventsForAdmin(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<String> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size);

    @PatchMapping("/{eventId}")
    EventFullDto updateEventForAdmin(@PathVariable("eventId") Long eventId,
                                     @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest);

    @GetMapping("/{eventId}")
    EventFullDto findEventById(@PathVariable("eventId") Long eventId);
}
