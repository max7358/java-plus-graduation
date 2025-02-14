package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatEventService statEventService;

    public CompilationDto find(Long compilationId) {
        Optional<Compilation> optional = compilationRepository.findById(compilationId);
        if (optional.isEmpty()) {
            throw new NotFoundException("Compilation not found");
        }
        return getCompilationWithEventViews(optional.get());
    }

    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Collection<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        if (newCompilationDto.getEvents() != null && newCompilationDto.getEvents().size() != events.size()) {
            throw new InvalidDataException("One or more events not found");
        }
        Compilation compilation = CompilationMapper.toModel(newCompilationDto, events);
        compilation = compilationRepository.save(compilation);
        log.info("Compilation saved: {}", compilation);
        return find(compilation.getId());
    }

    public void deleteCompilation(Long compilationId) {
        find(compilationId);
        compilationRepository.deleteById(compilationId);
        log.info("Compilation deleted, ID : {}", compilationId);
    }


    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compilationId) {
        Collection<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
        if (updateCompilationRequest.getEvents() != null && updateCompilationRequest.getEvents().size() != events.size()) {
            throw new InvalidDataException("One or more events not found");
        }
        Compilation currentCompilation = compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException("Compilation not found"));
        Compilation compilation = CompilationMapper.mergeModel(currentCompilation, updateCompilationRequest, events);
        if (compilationRepository.existsByTitleAndIdNot(compilation.getTitle(), compilationId)) {
            throw new InvalidDataException("Compilation name already exists");
        }
        compilation = compilationRepository.save(compilation);
        log.info("Compilation updated, ID : {}", compilationId);
        return getCompilationWithEventViews(compilation);
    }

    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Collection<Compilation> compilations = pinned == null
                ? compilationRepository.findAll(PageRequest.of(from / size, size)).stream().toList()
                : compilationRepository.findByPinned(pinned, PageRequest.of(from / size, size));
        return compilations.stream().map(this::getCompilationWithEventViews).collect(Collectors.toList());
    }

    private CompilationDto getCompilationWithEventViews(Compilation compilation) {
        Collection<Event> events = compilation.getEvents();
        return CompilationMapper.toDto(compilation, statEventService.getViews(events));
    }
}
