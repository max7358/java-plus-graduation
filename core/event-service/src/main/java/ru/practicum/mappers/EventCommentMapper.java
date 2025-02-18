package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.eventcomment.*;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.models.Event;
import ru.practicum.models.EventComment;

import java.time.LocalDateTime;

@UtilityClass
public class EventCommentMapper {
    public EventCommentDto toDto(EventComment model) {
        return new EventCommentDto(
                model.getId(),
                model.getText(),
                new UserShortDto(model.getAuthorId(), "dummy"),
                model.getCreated()
        );
    }

    public EventCommentPrivateDto toPrivateDto(EventComment model) {
        return new EventCommentPrivateDto(
                model.getId(),
                model.getText(),
                model.getEvent().getId(),
                model.getAuthorId(),
                model.getCreated(),
                model.getStatus()
        );
    }

    public EventComment toModel(CreateCommentRequest request, Long authorId, Event event) {
        EventComment model = new EventComment();
        model.setEvent(event);
        model.setAuthorId(authorId);
        model.setText(request.getText());
        model.setCreated(LocalDateTime.now());
        return model;
    }

    public EventComment mergeModel(UpdateCommentRequest request, EventComment model) {
        model.setText(request.getText());
        return model;
    }

    public EventComment mergeModel(UpdateCommentAdminRequest request, EventComment model) {
        if (request.getText() != null) {
            model.setText(request.getText());
        }
        if (request.getStatus() != null) {
            model.setStatus(request.getStatus());
        }
        return model;
    }
}
