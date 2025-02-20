package ru.practicum.stats;

import collector.ActionControllerGrpc;
import collector.UserAction;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class CollectorClient {
    @GrpcClient("collector")
    private ActionControllerGrpc.ActionControllerBlockingStub client;

    public void sendUserAction(long userId, long eventId, UserAction.ActionTypeProto userAction) {
        try {
            UserAction.UserActionProto actionProto = UserAction.UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(userAction)
                    .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                    .build();
            Empty empty = client.collectUserAction(actionProto);
        } catch (Exception e) {
            log.error("Failed to send user action to collector", e);
        }
    }
}
