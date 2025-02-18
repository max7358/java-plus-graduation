package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.CompilationClient;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.CompilationService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationController implements CompilationClient {
    private final CompilationService compilationService;

    public Collection<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    public CompilationDto find(@PathVariable Long compId) {
        return compilationService.find(compId);
    }
}
