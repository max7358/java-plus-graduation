package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@Configuration
@ConfigurationProperties("aggregator.kafka")
public class KafkaProperties {
    private Map<String, String> producerProperties;
    private Map<String, String> consumerProperties;

    @Value("${aggregator.kafka.topic.user}")
    String topicUsers;

    @Value("${aggregator.kafka.topic.event}")
    String topicEvents;

    private Properties getProducerConfig() {
        Properties config = new Properties();
        config.putAll(producerProperties);
        return config;
    }

    private Properties getConsumerConfig() {
        Properties config = new Properties();
        config.putAll(consumerProperties);
        return config;
    }

    @Bean
    public Producer<String, EventSimilarityAvro> kafkaProducer() {
        return new KafkaProducer<>(getProducerConfig());
    }

    @Bean
    public Consumer<String, UserActionAvro> kafkaConsumer() {
        return new KafkaConsumer<>(getConsumerConfig());
    }
}
