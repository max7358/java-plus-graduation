package ru.practicum.controller;

import collector.ActionControllerGrpc;
import collector.UserAction;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.handler.UserActionHandler;
import ru.practicum.service.KafkaProducerService;

@GrpcService
public class ActionController extends ActionControllerGrpc.ActionControllerImplBase {

    KafkaProducerService kafkaProducerService;

    @Override
    public void collectAction(UserAction.UserActionProto userAction, StreamObserver<Empty> responseObserver) {
        try {
            UserActionAvro actionAvro = UserActionHandler.toAvro(userAction);
            kafkaProducerService.sendAction(actionAvro);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
