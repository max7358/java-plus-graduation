package ru.practicum.handler;

import collector.UserAction;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;

@UtilityClass
public class UserActionHandler {
    public UserActionAvro toAvro(UserAction.UserActionProto userActionProto) {
        return UserActionAvro.newBuilder().setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds()))
                .setActionType(ActionTypeAvro.valueOf(userActionProto.getActionType().name())).build();
    }
}
