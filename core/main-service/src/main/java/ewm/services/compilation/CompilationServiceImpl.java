package ewm.services.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ewm.dto.compilation.CompilationDto;
import ewm.dto.compilation.NewCompilationDto;
import ewm.dto.compilation.UpdateCompilationRequest;
import ewm.exceptions.InvalidDataException;
import ewm.exceptions.NotFoundException;
import ewm.mappers.CompilationMapper;
import ewm.models.Compilation;
import ewm.models.Event;
import ewm.repositories.CompilationRepository;
import ewm.repositories.EventRepository;
import ewm.services.StatEventService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
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

    @Override
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

    @Override
    public void deleteCompilation(Long compilationId) {
        find(compilationId);
        compilationRepository.deleteById(compilationId);
        log.info("Compilation deleted, ID : {}", compilationId);
    }

    @Override
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

    @Override
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
