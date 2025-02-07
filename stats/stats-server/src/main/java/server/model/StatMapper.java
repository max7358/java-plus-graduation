package server.model;

import lombok.experimental.UtilityClass;
import dto.StatDto;

@UtilityClass
public class StatMapper {
    public StatDto toDto(Stat model) {
        StatDto dto = new StatDto();
        dto.setApp(model.getApp());
        dto.setUri(model.getUri());
        dto.setHits(model.getHits());
        return dto;
    }
}
