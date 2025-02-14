package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventCommentAdminClient;
import ru.practicum.dto.eventcomment.EventCommentPrivateDto;
import ru.practicum.dto.eventcomment.UpdateCommentAdminRequest;
import ru.practicum.services.EventCommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
public class EventCommentAdminController implements EventCommentAdminClient {

    private final EventCommentService eventCommentService;

    @Autowired
    public EventCommentAdminController(EventCommentService eventCommentService) {
        this.eventCommentService = eventCommentService;
    }

    public List<EventCommentPrivateDto> get(@RequestParam(required = false) Long eventId,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(defaultValue = "0") Integer from) {
        return eventCommentService.getComments(eventId, size, from);
    }

    public EventCommentPrivateDto update(@PathVariable("commentId") Long commentId,
                                         @Valid @RequestBody UpdateCommentAdminRequest request
    ) {
        return eventCommentService.update(commentId, request);
    }
}
