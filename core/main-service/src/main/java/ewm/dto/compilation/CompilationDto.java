package ewm.dto.compilation;

import ewm.dto.event.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private Collection<EventShortDto> events;
}
