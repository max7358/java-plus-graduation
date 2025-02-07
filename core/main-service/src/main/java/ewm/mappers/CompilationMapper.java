package ewm.mappers;

import ewm.dto.compilation.CompilationDto;
import ewm.dto.compilation.NewCompilationDto;
import ewm.dto.compilation.UpdateCompilationRequest;
import ewm.dto.event.EventShortDto;
import ewm.models.Compilation;
import ewm.models.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation model, Map<Long, Long> views) {
        Collection<EventShortDto> events = new ArrayList<>();
        if (model.getEvents() != null) {
            events = model.getEvents().stream()
                    .map(event -> EventMapper.toShortDto(event, views.get(event.getId())))
                    .collect(Collectors.toList());
        }
        return new CompilationDto(
                model.getId(),
                model.getTitle(),
                model.getPinned(),
                events
        );
    }

    public static Compilation toModel(NewCompilationDto dto, Collection<Event> events) {
        Compilation model = new Compilation();
        model.setTitle(dto.getTitle());
        model.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        if (!events.isEmpty()) {
            model.setEvents(events);
        }
        return model;
    }

    public static Compilation mergeModel(Compilation model, UpdateCompilationRequest dto, Collection<Event> events) {
        if (dto.getTitle() != null) {
            model.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            model.setPinned(dto.getPinned());
        }
        if (!events.isEmpty()) {
            model.setEvents(events);
        }
        return model;
    }
}
