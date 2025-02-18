package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@Configuration
@ConfigurationProperties("collector.kafka.producer")
public class KafkaProperties {
    private Map<String, String> properties;

    @Value("${collector.kafka.topic.user}")
    String topicUser;


    private Properties getConfig() {
        Properties config = new Properties();
        config.putAll(properties);
        return config;
    }

    @Bean
    public Producer<String, SpecificRecordBase> kafkaProducer() {
        return new KafkaProducer<>(getConfig());
    }
}
