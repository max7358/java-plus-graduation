package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventrequest.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "request-service")
public interface RequestClient {
    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getRequests(@PathVariable Long userId);

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId);

    @GetMapping("/users/{userId}/requests/{eventId}")
    List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long userId, @PathVariable Long eventId);

    @PostMapping("/users/{userId}/requests/update")
    void updateRequest(@PathVariable Long userId, ParticipationRequestDto eventRequest);
}
