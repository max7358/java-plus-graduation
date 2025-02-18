package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventcomment.EventCommentPrivateDto;
import ru.practicum.dto.eventcomment.UpdateCommentAdminRequest;

import java.util.List;

@FeignClient(name = "event-service", contextId = "event-comment-admin", path = "/admin/comments")
public interface EventCommentAdminClient {
    @GetMapping
    List<EventCommentPrivateDto> get(@RequestParam(required = false) Long eventId,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer from);

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    EventCommentPrivateDto update(@PathVariable("commentId") Long commentId,
                                  @Valid @RequestBody UpdateCommentAdminRequest request
    );
}
