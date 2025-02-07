package ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ewm.dto.eventComment.*;
import ewm.enums.EventCommentStatus;
import ewm.enums.EventState;
import ewm.exceptions.BadRequestException;
import ewm.exceptions.ForbiddenException;
import ewm.exceptions.InvalidDataException;
import ewm.exceptions.NotFoundException;
import ewm.helpers.PaginateHelper;
import ewm.mappers.EventCommentMapper;
import ewm.models.Event;
import ewm.models.EventComment;
import ewm.models.User;
import ewm.repositories.EventCommentRepository;
import ewm.repositories.EventRepository;
import ewm.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EventCommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;

    public EventCommentService(UserRepository userRepository,
                               EventRepository eventRepository,
                               EventCommentRepository eventCommentRepository
    ) {
        this.userRepository = userRepository;
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
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event not published");
        }
        EventComment comment = eventCommentRepository.save(EventCommentMapper.toModel(request, author, event));
        return EventCommentMapper.toPrivateDto(comment);
    }


    @Transactional
    public EventCommentPrivateDto update(Long userId, Long commentId, UpdateCommentRequest request) {
        EventComment comment = find(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
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
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("No access to comment");
        }
        if (!comment.getStatus().equals(EventCommentStatus.PENDING)) {
            throw new BadRequestException("The comment must be in draft");
        }
        eventCommentRepository.delete(comment);
    }
}
