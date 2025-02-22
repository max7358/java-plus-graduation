package ru.practicum.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.UserService;

@Component
public class UserConsumer {

    private final UserService userService;

    public UserConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "${analyzer.kafka.topic.user}", containerFactory = "userKafkaListenerContainerFactory")
    public void consumeEventSimilarity(UserActionAvro action) {
        userService.saveUserAction(action);
    }
}
