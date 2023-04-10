package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User postUser(@NotNull @Valid @RequestBody User user) {
        log.info("POST request received: {}", user);
        userService.addUser(user);
        return user;
    }

    @PutMapping
    public User putUser(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
        userService.updateUser(user);
        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

}
