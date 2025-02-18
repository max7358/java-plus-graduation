package ru.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventCommentClient;
import ru.practicum.dto.eventcomment.EventCommentDto;
import ru.practicum.services.EventCommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events/{eventId}/comments")
public class EventCommentController implements EventCommentClient {

    private final EventCommentService eventCommentService;

    @Autowired
    public EventCommentController(EventCommentService eventCommentService) {
        this.eventCommentService = eventCommentService;
    }

    public List<EventCommentDto> get(@PathVariable("eventId") Long eventId,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer from) {
        return eventCommentService.getPublishedComments(eventId, size, from);
    }
}
