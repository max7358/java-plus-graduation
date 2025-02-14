package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;

import java.util.Collection;

@FeignClient(name = "event-service", contextId = "compilation-public", path = "/compilations")
public interface CompilationClient {
    @GetMapping
    Collection<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/{compId}")
    CompilationDto find(@PathVariable Long compId);
}
