package ru.practicum.stats;

import analyzer.Recommendation;
import analyzer.RecommendationsControllerGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AnalyzerClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public Stream<Recommendation.RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        Recommendation.SimilarEventsRequestProto request = Recommendation.SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        // gRPC-метод getSimilarEvents возвращает Iterator, потому что в его схеме
        // мы указали, что он должен вернуть поток сообщений (stream stats.message.RecommendedEventProto)
        Iterator<Recommendation.RecommendedEventProto> iterator = client.getSimilarEvents(request);

        // преобразуем Iterator в Stream
        return asStream(iterator);
    }

    public Stream<Recommendation.RecommendedEventProto> getRecommendationsForUser(long userId, long maxResults) {
        Recommendation.UserPredictionsRequestProto request = Recommendation.UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        Iterator<Recommendation.RecommendedEventProto> iterator = client.getRecommendationsForUser(request);
        return asStream(iterator);
    }

    public Stream<Recommendation.RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        Recommendation.InteractionsCountRequestProto.Builder builder =
                Recommendation.InteractionsCountRequestProto.newBuilder();
        eventIds.forEach(builder::addEventId);
        Iterator<Recommendation.RecommendedEventProto> iterator = client.getInteractionsCount(builder.build());
        return asStream(iterator);
    }

    private Stream<Recommendation.RecommendedEventProto> asStream(Iterator<Recommendation.RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
