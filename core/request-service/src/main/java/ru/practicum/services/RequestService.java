package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventAdminClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.eventrequest.ParticipationRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.EventRequestStatus;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventRequestMapper;
import ru.practicum.models.EventRequest;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventAdminClient eventClient;
    private final UserClient userClient;

    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        EventFullDto event = getEvent(eventId);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new InvalidDataException("Request already exist");
        if (event.getInitiator().getId().equals(userId)) {
            throw new InvalidDataException("Request can't be created by initiator");
        }
        if (event.getPublishedOn() == null) {
            throw new InvalidDataException("Event not yet published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();
        if (event.getParticipantLimit() > 0 && !event.getRequestModeration() && event.getParticipantLimit() <= requestsSize) {
            throw new InvalidDataException("Participant limit exceeded");
        }

        EventRequest eventRequest = new EventRequest(null, eventId, userId, LocalDateTime.now(), EventRequestStatus.PENDING);
        if (!event.getRequestModeration()) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
        }

        if (event.getParticipantLimit() != null && event.getParticipantLimit() == 0) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
        }

        return EventRequestMapper.toDto(requestRepository.save(eventRequest));
    }

    public List<ParticipationRequestDto> getRequests(Long userId) {
        getUser(userId);
        return EventRequestMapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    private UserDto getUser(Long userId) {
        return userClient.getUser(List.of(userId), 1, 0).getFirst();
    }

    private EventFullDto getEvent(Long eventId) {
        return eventClient.findEventById(eventId);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(EventRequestStatus.CANCELED);
        return EventRequestMapper.toDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getRequestsByEventId(Long eventId, Long userId) {
        return EventRequestMapper.toDto(requestRepository.findAllByEventId(eventId));
    }

    public void updateRequest(Long userId, ParticipationRequestDto eventRequest) {
        List<EventRequest> requests = requestRepository.findAllByEventIdWithInitiatorId(eventRequest.getEvent(), eventRequest.getRequester());
        requests.stream().peek(request -> request.setStatus(eventRequest.getStatus())).forEach(requestRepository::save);
    }
}
