package ru.practicum.admin.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.services.UserService;
import ru.practicum.admin.params.user.UserAdminParam;
import ru.practicum.client.UserClient;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
public class UserController implements UserClient {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserDto addUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.addUser(userRequest);
    }

    public List<UserDto> getUser(@RequestParam(required = false) List<Long> ids,
                          @RequestParam(defaultValue = "10") Integer size,
                          @RequestParam(defaultValue = "0") Integer from) {
        return userService.getUsers(new UserAdminParam(ids, size, from));
    }

    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
