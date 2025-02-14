package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventcomment.CreateCommentRequest;
import ru.practicum.dto.eventcomment.EventCommentPrivateDto;
import ru.practicum.dto.eventcomment.UpdateCommentRequest;

import java.util.List;

@FeignClient(name = "event-service", contextId = "event-comment-private", path = "/users/{userId}/comments")
public interface EventCommentPrivateClient {
    @GetMapping
    List<EventCommentPrivateDto> get(@PathVariable("userId") Long userId,
                                     @RequestParam(required = false) Long eventId,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer from);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventCommentPrivateDto create(@PathVariable("userId") Long userId, @Valid @RequestBody CreateCommentRequest request);

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    EventCommentPrivateDto update(@PathVariable("userId") Long userId,
                                  @PathVariable("commentId") Long commentId,
                                  @Valid @RequestBody UpdateCommentRequest request
    );

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("userId") Long userId, @PathVariable("commentId") Long commentId);
}
