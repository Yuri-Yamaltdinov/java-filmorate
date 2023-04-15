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

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.info("PUT request received: userId {}, friend Id {}", id, friendId);
        return userService.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.info("DELETE request received: userId {}, friend Id {}", id, friendId);
        return userService.removeFromFriends(id, friendId);
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("GET request received");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("GET request received: getUserById {}", id);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Integer id) {
        log.info("GET request received: {}/friends", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer friendId) {
        log.info("GET request received: /{}/friends/common/{}", id, friendId);
        return userService.getCommonFriends(id, friendId);
    }

}
