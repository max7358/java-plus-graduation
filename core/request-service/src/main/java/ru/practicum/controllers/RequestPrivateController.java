package ru.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.RequestClient;
import ru.practicum.dto.eventrequest.ParticipationRequestDto;
import ru.practicum.services.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
public class RequestPrivateController implements RequestClient {

    private final RequestService requestService;

    public RequestPrivateController(RequestService requestService) {
        this.requestService = requestService;
    }

    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        return requestService.getRequests(userId);
    }

    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByEventId(eventId, userId);
    }

    public void updateRequest(Long userId, ParticipationRequestDto eventRequest) {
        requestService.updateRequest(userId, eventRequest);
    }
}
