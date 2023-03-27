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
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();
    private int id = 0;

    @PostMapping
    public User postUser(@NotNull @Valid @RequestBody User user) {
        log.info("POST request received: {}", user);
        checkEmail(user);
        checkBirthday(user);
        checkName(user);
        addUser(user);
        return user;
    }

    @PutMapping
    public User putUser(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
        checkBirthday(user);
        checkName(user);
        updateUser(user);
        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Current number of users: {}", users.size());
        return users.values();
    }

    private void checkEmail(User user) {
        if (userEmails.contains(user.getEmail())) {
            log.error("User email already exists");
            throw new ValidationException("User with such email already exists");
        }
    }

    private void checkBirthday(User user) {
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday date is in the future");
            throw new ValidationException("User's birthday is in the future: " + user.getBirthday());
        }
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name is empty. Setting login {} as user name", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void addUser(User user) {
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());
        log.info("User added: {}", user);
    }

    private void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new ValidationException("User with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
    }
}
