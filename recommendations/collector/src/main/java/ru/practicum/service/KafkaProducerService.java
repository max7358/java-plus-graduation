package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.config.KafkaProperties;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.concurrent.Future;

@Slf4j
@Service
public class KafkaProducerService {

    private final Producer<String, SpecificRecordBase> kafkaProducer;
    private final String topicUser;

    @Autowired
    KafkaProducerService(KafkaProperties kafkaProperties) {
        this.kafkaProducer = kafkaProperties.kafkaProducer();
        this.topicUser = kafkaProperties.getTopicUser();
    }

    public void sendAction(UserActionAvro actionAvro) {
        sendMessage(topicUser, actionAvro);
    }

    private void sendMessage(String topic, SpecificRecordBase recordBase) {
        try {
            Future<RecordMetadata> send = kafkaProducer.send(new ProducerRecord<>(topic, recordBase));
            if (send.isDone()) {
                send.resultNow();
            }
        } catch (SerializationException e) {
            log.error("Message serialization failed: {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Message send failed: {}", e.getMessage());
        }
    }

}
