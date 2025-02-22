package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUserAction(UserActionAvro action) {
        userRepository.save(UserActionMapper.toModel(action));
    }
}
