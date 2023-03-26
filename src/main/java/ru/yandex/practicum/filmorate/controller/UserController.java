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
        //checkEmail(user);
        checkBirthday(user);
        if (userEmails.contains(user.getEmail())) {
            log.error("User email already exists");
            throw new ValidationException("User with such email already exists");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name is empty. Setting login {} as user name", user.getLogin());
            user.setName(user.getLogin());
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());
        log.info("User added: {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
        //checkEmail(user);
        checkBirthday(user);
        if (user.getName() == null || user.getName().isBlank()) {
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

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Текущее количество пользователей {}", users.size());
        return users.values();
    }

/*    private void checkEmail(User user) {
        if (!user.getEmail().contains("@")) {
            log.error("User email has incorrect format");
            throw new ValidationException("User email has incorrect format");
        }
    }*/

    private void checkBirthday(User user) {
        if (user.getBirthday() == null) {
            log.warn("User's birthday does not exist");
            return;
        }
        if (!user.getBirthday().isBefore(LocalDate.now())) {
            log.error("Birthday date is in the future");
            throw new ValidationException("User's birthday is in the future: " + user.getBirthday());
        }
    }

}
