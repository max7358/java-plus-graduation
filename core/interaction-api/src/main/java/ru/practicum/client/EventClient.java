package ru.practicum.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventSort;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@FeignClient(name = "event-service", contextId = "event-public", path = "/events")
public interface EventClient {
    @GetMapping
    Collection<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(required = false) Boolean onlyAvailable,
                                        @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        HttpServletRequest request);

    @GetMapping("/{id}")
    EventFullDto find(@PathVariable Long id, HttpServletRequest request);
}
