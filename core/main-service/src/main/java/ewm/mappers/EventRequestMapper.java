package ewm.mappers;

import lombok.experimental.UtilityClass;
import ewm.dto.eventRequest.ParticipationRequestDto;
import ewm.models.EventRequest;

import java.util.List;

@UtilityClass
public class EventRequestMapper {
    public ParticipationRequestDto toDto(EventRequest model) {
        return new ParticipationRequestDto(
                model.getId(),
                model.getCreated(),
                model.getEvent().getId(),
                model.getRequester().getId(),
                model.getStatus()
        );
    }

    public List<ParticipationRequestDto> toDto(List<EventRequest> models) {
        return models.stream().map(EventRequestMapper::toDto).toList();
    }
}
