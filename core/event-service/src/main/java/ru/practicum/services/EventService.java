package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.RequestClient;
import ru.practicum.dto.event.*;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.ParticipationRequestDto;
import ru.practicum.enums.EventRequestStatus;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.EventState;
import ru.practicum.mappers.EventMapper;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.repositories.EventRepository.EventSpecification.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestClient requestClient;
    private final StatEventService statEventService;

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = eventRepository.save(EventMapper.toModel(newEventDto, userId));
        log.info("Added event: {}", event);
        return EventMapper.toDto(event, 0L);
    }

    public List<EventShortDto> getPrivateEvents(Long userId, PageRequest request) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, request);
        return events.stream().map(event -> EventMapper.toShortDto(event, statEventService.getViews(event))).toList();
    }

    public EventFullDto getPrivateEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toDto(event, statEventService.getViews(event));
    }

    public EventFullDto updatePrivateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventRequest) {
        Event oldEvent = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!oldEvent.getState().equals(EventState.CANCELED) && !oldEvent.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Event with id=" + eventId + " cannot be updated");
        }
        Event updatedEvent = EventMapper.mergeModel(oldEvent, updateEventRequest);
        return EventMapper.toDto(eventRepository.save(updatedEvent), statEventService.getViews(oldEvent));
    }

    public EventFullDto find(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event with id=" + eventId + " was not found"));
        return EventMapper.toDto(event, statEventService.getViews(event));
    }

    public Collection<EventShortDto> getEvents(EventFilterDto filter, EventSort sort, Integer from, Integer size) {
        List<Category> categories = categoryRepository.findByIdIn(filter.getCategories());
        if (filter.getCategories() != null && filter.getCategories().size() != categories.size()) {
            throw new BadRequestException("One or more categories not found");
        }
        Specification<Event> specification = byText(filter.getText())
                .and(byCategories(categories))
                .and(byPaid(filter.getPaid()))
                .and(byRangeStart(filter.getRangeStart()))
                .and(byRangeEnd(filter.getRangeEnd()))
                .and(byOnlyAvailable(filter.getOnlyAvailable()))
                .and(byState(EventState.PUBLISHED));
        Collection<Event> events = eventRepository.findAll(specification,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"))
        ).toList();
        Map<Long, Long> views = statEventService.getViews(events);
        Collection<EventShortDto> eventShortDtoCollection = events.stream()
                .map(event -> EventMapper.toShortDto(event, views.get(event.getId())))
                .toList();
        if (sort.equals(EventSort.VIEWS)) {
            return eventShortDtoCollection.stream().sorted(Comparator.comparing(EventShortDto::getViews)).toList();
        }
        return eventShortDtoCollection;
    }


    public List<EventFullDto> getAdminEvents(EventAdminFilterDto filter) {
        log.info("Executing getAdminEvents with filter: {}", filter);

        Pageable pageable = PageRequest.of(filter.getFrom(), filter.getSize());

        List<Event> events = eventRepository.findAllByFilters(
              //  filter.getUsers(),
                filter.getStates(),
                filter.getCategories(),
                filter.getRangeStart(),
                filter.getRangeEnd(),
                pageable
        );
        log.info("Events found: {}", events.size());


        return events.stream()
                .map(EventMapper::toDtoWithoutViews)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateAdminRequest) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event with id=" + eventId + " is PUBLISHED");
        }
        if (oldEvent.getState().equals(EventState.CANCELED)) {
            throw new InvalidDataException("Event with id=" + eventId + " is CANCELED");
        }
        Event updatedEvent = EventMapper.mergeModel(oldEvent, updateAdminRequest);

        return EventMapper.toDto(eventRepository.save(updatedEvent), 0L);
    }

    public EventFullDto findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .map(event -> EventMapper.toDto(event, statEventService.getViews(event)))
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    public List<ParticipationRequestDto> getPrivateRequests(Long userId, Long eventId) {
        return requestClient.getRequestsByEventId(userId, eventId);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        if (event.getParticipantLimit() == 0) {
            return updateResult;
        }

        List<ParticipationRequestDto> eventRequests = requestClient.getRequestsByEventId(userId, eventId);
        if (eventRequests.isEmpty()) {
            return updateResult;
        }

        List<ParticipationRequestDto> requestUpdateList = eventRequests.stream()
                .filter(eventRequest -> updateRequest.getRequestIds().contains(eventRequest.getId())).toList();
        requestUpdateList.forEach(eventRequest -> {
            if (eventRequest.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                throw new ConflictException("Request can't REJECT confirmed request");
            }
            if (!eventRequest.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new BadRequestException("Request must have status PENDING");
            }

            if (event.getConfirmedRequests() != null && (event.getConfirmedRequests() >= event.getParticipantLimit())) {
                eventRequest.setStatus(EventRequestStatus.REJECTED);
                updateResult.getRejectedRequests().add(eventRequest);
                requestClient.updateRequest(userId, eventRequest);
                throw new ConflictException("The participant limit has been reached");
            }
            eventRequest.setStatus(updateRequest.getStatus());
            if (updateRequest.getStatus().equals(EventRequestStatus.REJECTED)) {
                updateResult.getRejectedRequests().add(eventRequest);
            } else {
                updateResult.getConfirmedRequests().add(eventRequest);
            }
            requestClient.updateRequest(userId, eventRequest);
            event.setConfirmedRequests(event.getConfirmedRequests() == null ? 1 : event.getConfirmedRequests() + 1);
        });

        eventRepository.save(event);
        return updateResult;
    }
}
