package ru.practicum.controller;

import analyzer.Recommendation;
import analyzer.RecommendationsControllerGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.service.RecommendationsService;

import java.util.List;

@GrpcService
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationsService recommendationsService;

    public RecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    @Override
    public void getRecommendationsForUser(Recommendation.UserPredictionsRequestProto request,
                                          StreamObserver<Recommendation.RecommendedEventProto> responseObserver) {
        long userId = request.getUserId();
        long maxResults = request.getMaxResults();
        List<Recommendation.RecommendedEventProto> recommendations = recommendationsService.getRecommendationsForUser(userId, maxResults);

        recommendations.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(Recommendation.SimilarEventsRequestProto request,
                                 StreamObserver<Recommendation.RecommendedEventProto> responseObserver) {
        long eventId = request.getEventId();
        long userId = request.getUserId();
        long maxResults = request.getMaxResults();

        List<Recommendation.RecommendedEventProto> similarEvents =
                recommendationsService.getSimilarEvents(eventId, userId, maxResults);

        similarEvents.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(Recommendation.InteractionsCountRequestProto request,
                                     StreamObserver<Recommendation.RecommendedEventProto> responseObserver) {
        List<Long> eventIds = request.getEventIdList();

        List<Recommendation.RecommendedEventProto> interactionCounts = recommendationsService.getInteractionCounts(eventIds);
        interactionCounts.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
