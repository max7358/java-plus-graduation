package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Getter
@Setter
@Configuration
@ConfigurationProperties("analyzer.kafka.consumer")
@EnableKafka
public class KafkaProperties {
    private Properties userProperties;
    private Properties eventProperties;

    @Value("${analyzer.kafka.topic.user}")
    String topicUsers;

    @Value("${analyzer.kafka.topic.event}")
    String topicEvents;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    eventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventConsumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> userConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerUserConfigs());
    }

    @Bean
    public ConsumerFactory<Integer, String> eventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerEventConfigs());
    }

    @Bean
    public Map<String, Object> consumerUserConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, userProperties.getProperty(BOOTSTRAP_SERVERS_CONFIG));
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, userProperties.getProperty(KEY_DESERIALIZER_CLASS_CONFIG));
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, userProperties.getProperty(VALUE_DESERIALIZER_CLASS_CONFIG));
        props.put(GROUP_ID_CONFIG, userProperties.getProperty(GROUP_ID_CONFIG));
        return props;
    }

    @Bean
    public Map<String, Object> consumerEventConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, eventProperties.getProperty(BOOTSTRAP_SERVERS_CONFIG));
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, eventProperties.getProperty(KEY_DESERIALIZER_CLASS_CONFIG));
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, eventProperties.getProperty(VALUE_DESERIALIZER_CLASS_CONFIG));
        props.put(GROUP_ID_CONFIG, eventProperties.getProperty(GROUP_ID_CONFIG));
        return props;
    }
}
