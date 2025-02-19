package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaProperties;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.AggregationService;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final Producer<String, EventSimilarityAvro> producer;
    private final Consumer<String, UserActionAvro> consumer;
    private final String topicUsers;
    private final String topicEvents;
    private final Map<String, EventSimilarityAvro> similarities = new HashMap<>();
    private AggregationService aggregationService;

    @Autowired
    AggregationStarter(KafkaProperties kafkaProperties, AggregationService aggregationService) {
        this.producer = kafkaProperties.kafkaProducer();
        this.consumer = kafkaProperties.kafkaConsumer();
        this.topicUsers = kafkaProperties.getTopicUsers();
        this.topicEvents = kafkaProperties.getTopicEvents();
        this.aggregationService = aggregationService;
    }

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(topicUsers));

            while (true) {
                ConsumerRecords<String, UserActionAvro> consumerRecords = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, UserActionAvro> consumerRecord : consumerRecords) {
                    log.debug("Processing record with key: {}, value: {}", consumerRecord.key(), consumerRecord.value());
                    aggregationService.updateState(consumerRecord.value()).ifPresent(esal -> esal.forEach(esa -> {
                        try {
                            producer.send(new ProducerRecord<>(topicEvents, esa));
                        } catch (Exception e) {
                            log.error("Message send failed: {}", e.getMessage());
                        }
                    }));
                }
            }

        } catch (WakeupException ignored) {
            log.info("Shutdown signal received. Exiting...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Error during closing resources", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

}