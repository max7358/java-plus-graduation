package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventcomment.EventCommentDto;

import java.util.List;

@FeignClient(name = "event-service", contextId = "event-comment-public", path = "/events/{eventId}/comments")
public interface EventCommentClient {
    @GetMapping
    List<EventCommentDto> get(@PathVariable("eventId") Long eventId,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam(defaultValue = "0") Integer from);
}
