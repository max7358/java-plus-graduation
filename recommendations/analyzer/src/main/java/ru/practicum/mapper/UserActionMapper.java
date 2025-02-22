package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;

@UtilityClass
public class UserActionMapper {
    public UserAction toModel(UserActionAvro avro) {
        return UserAction.builder()
                .userId(avro.getUserId())
                .eventId(avro.getEventId())
                .actionType(convert(avro.getActionType()))
                .timestamp(avro.getTimestamp())
                .build();
    }

    private double convert(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 1;
            case REGISTER -> 2;
            case LIKE -> 3;
            default -> 0.0;
        };
    }
}
