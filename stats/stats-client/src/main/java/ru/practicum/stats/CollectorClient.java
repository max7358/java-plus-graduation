package ru.practicum.stats;

import collector.ActionControllerGrpc;
import collector.UserAction;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CollectorClient {
    @GrpcClient("collector")
    private ActionControllerGrpc.ActionControllerBlockingStub client;

    public void sendUserAction(long userId, long eventId, UserAction.ActionTypeProto userAction) {
        UserAction.UserActionProto actionProto = UserAction.UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(userAction)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .build();
        Empty empty = client.collectUserAction(actionProto);
    }
}
