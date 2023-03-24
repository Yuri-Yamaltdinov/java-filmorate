package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private Set<String> userEmails = new HashSet<>();
    private int id = 0;

    //создание пользователя POST /user
    @PostMapping
    public User postUser(@NotNull @Valid @RequestBody User user) {
        log.info("POST request received: {}", user);
        if (userEmails.contains(user.getEmail())) {
            log.error("User email already exists");
            throw new ValidationException("User with such email already exists");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name is empty. Setting login {} as user name", user.getLogin());
            user.setName(user.getLogin());
        }
        if (!user.getBirthday().isBefore(LocalDate.now())) {
            log.error("Birthday date is in the future");
            throw new ValidationException("User's birthday is in the future: " + user.getBirthday());
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());
        log.info("User added: {}", user);
        return user;
    }
    //обновление пользователя PUT /user
    @PutMapping
    public User putUser(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name is empty. Setting login {} as user name", user.getLogin());
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new ValidationException("User with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;
    }
    //получение списка всех пользователей GET /users
    @GetMapping
    public Collection<User> getUsers() {
        log.info("Текущее количество пользователей {}", users.size());
        return users.values();
    }
}
