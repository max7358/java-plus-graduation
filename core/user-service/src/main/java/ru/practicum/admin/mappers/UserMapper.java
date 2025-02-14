package ru.practicum.admin.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.admin.models.User;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

@UtilityClass
public class UserMapper {
    public static UserShortDto toShortDto(User model) {
        return new UserShortDto(
                model.getId(),
                model.getName()
        );
    }

    public UserDto toDto(User model) {
        return new UserDto(
                model.getId(),
                model.getEmail(),
                model.getName()
        );
    }

    public List<UserDto> toDto(List<User> models) {
        return models.stream().map(UserMapper::toDto).toList();

    }

    public User toModel(NewUserRequest dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        return user;
    }

    public User toModel(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        return user;
    }
}
