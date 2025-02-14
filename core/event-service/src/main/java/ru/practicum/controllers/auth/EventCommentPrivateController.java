package ru.practicum.controllers.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventCommentPrivateClient;
import ru.practicum.dto.eventcomment.CreateCommentRequest;
import ru.practicum.dto.eventcomment.EventCommentPrivateDto;
import ru.practicum.dto.eventcomment.UpdateCommentRequest;
import ru.practicum.services.EventCommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class EventCommentPrivateController implements EventCommentPrivateClient {

    private final EventCommentService eventCommentService;

    @Autowired
    public EventCommentPrivateController(EventCommentService eventCommentService) {
        this.eventCommentService = eventCommentService;
    }

    public List<EventCommentPrivateDto> get(@PathVariable("userId") Long userId,
                                            @RequestParam(required = false) Long eventId,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(defaultValue = "0") Integer from) {
        return eventCommentService.getComments(userId, eventId, size, from);
    }

    public EventCommentPrivateDto create(@PathVariable("userId") Long userId, @Valid @RequestBody CreateCommentRequest request) {
        return eventCommentService.create(userId, request);
    }

    public EventCommentPrivateDto update(@PathVariable("userId") Long userId,
                                         @PathVariable("commentId") Long commentId,
                                         @Valid @RequestBody UpdateCommentRequest request
    ) {
        return eventCommentService.update(userId, commentId, request);
    }

    public void delete(@PathVariable("userId") Long userId, @PathVariable("commentId") Long commentId) {
        eventCommentService.delete(userId, commentId);
    }
}
