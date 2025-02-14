package ru.practicum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserClient;
import ru.practicum.dto.eventcomment.*;
import ru.practicum.enums.EventCommentStatus;
import ru.practicum.enums.EventState;
import ru.practicum.helpers.PaginateHelper;
import ru.practicum.mappers.EventCommentMapper;
import ru.practicum.models.Event;
import ru.practicum.models.EventComment;
import ru.practicum.repositories.EventCommentRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EventCommentService {
    private final UserClient userClient;
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;

    public EventCommentService(UserClient userClient,
                               EventRepository eventRepository,
                               EventCommentRepository eventCommentRepository
    ) {
        this.userClient = userClient;
        this.eventRepository = eventRepository;
        this.eventCommentRepository = eventCommentRepository;
    }

    public List<EventCommentPrivateDto> getComments(Long userId, Long eventId, Integer size, Integer from) {
        if (eventId != null) {
            return eventCommentRepository.findAllByAuthorIdAndEventId(userId,
                            eventId,
                            PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                    ).stream()
                    .map(EventCommentMapper::toPrivateDto)
                    .collect(Collectors.toList());
        }
        return eventCommentRepository.findAllByAuthorId(userId,
                        PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                ).stream()
                .map(EventCommentMapper::toPrivateDto)
                .collect(Collectors.toList());
    }

    public List<EventCommentPrivateDto> getComments(Long eventId, Integer size, Integer from) {
        if (eventId != null) {
            return eventCommentRepository.findAllByEventId(eventId,
                            PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                    ).stream()
                    .map(EventCommentMapper::toPrivateDto)
                    .collect(Collectors.toList());
        }
        return eventCommentRepository.findAll(PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))).stream()
                .map(EventCommentMapper::toPrivateDto)
                .collect(Collectors.toList());
    }

    public List<EventCommentDto> getPublishedComments(Long eventId, Integer size, Integer from) {
        return eventCommentRepository.findAllByEventIdAndStatus(eventId, EventCommentStatus.PUBLISHED,
                        PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                ).stream()
                .map(EventCommentMapper::toDto)
                .collect(Collectors.toList());
    }

    public EventComment find(Long eventId) {
        return eventCommentRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event's comment not found"));
    }


    @Transactional
    public EventCommentPrivateDto create(Long userId, CreateCommentRequest request) {
        //User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event not published");
        }
        EventComment comment = eventCommentRepository.save(EventCommentMapper.toModel(request, userId, event));
        return EventCommentMapper.toPrivateDto(comment);
    }


    @Transactional
    public EventCommentPrivateDto update(Long userId, Long commentId, UpdateCommentRequest request) {
        EventComment comment = find(commentId);
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("No access to comment");
        }
        if (!comment.getStatus().equals(EventCommentStatus.PENDING)) {
            throw new BadRequestException("The comment must be in draft");
        }
        comment = eventCommentRepository.save(EventCommentMapper.mergeModel(request, comment));
        return EventCommentMapper.toPrivateDto(comment);
    }


    @Transactional
    public EventCommentPrivateDto update(Long commentId, UpdateCommentAdminRequest request) {
        EventComment comment = find(commentId);
        comment = eventCommentRepository.save(EventCommentMapper.mergeModel(request, comment));
        return EventCommentMapper.toPrivateDto(comment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        EventComment comment = find(commentId);
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("No access to comment");
        }
        if (!comment.getStatus().equals(EventCommentStatus.PENDING)) {
            throw new BadRequestException("The comment must be in draft");
        }
        eventCommentRepository.delete(comment);
    }
}
