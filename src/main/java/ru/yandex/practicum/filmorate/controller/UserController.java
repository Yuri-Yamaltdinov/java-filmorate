package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipsService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final FriendshipsService friendshipsService;

    @Autowired
    public UserController(UserService userService, FriendshipsService friendshipsService) {
        this.userService = userService;
        this.friendshipsService = friendshipsService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET request received");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        log.info("GET request received: getUserById {}", id);
        return userService.findById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriendsById(@PathVariable Integer id) {
        log.info("GET request received: {}/friends", id);
        return friendshipsService.findFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer friendId) {
        log.info("GET request received: /{}/friends/common/{}", id, friendId);
        return friendshipsService.findCommonFriends(id, friendId);
    }

    @PostMapping
    public User create(@NotNull @Valid @RequestBody User user) {
        log.info("POST request received: {}", user);
        userService.create(user);
        return user;
    }

    @PutMapping
    public User update(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
        userService.update(user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.info("PUT request received: userId {}, friend Id {}", id, friendId);
        friendshipsService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.info("DELETE request received: userId {}, friend Id {}", id, friendId);
        friendshipsService.removeFriend(id, friendId);
    }

}
