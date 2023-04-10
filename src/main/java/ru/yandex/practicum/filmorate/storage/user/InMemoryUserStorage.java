package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();
    private int id = 0;

    @Override
    public void addUser(User user) {
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());
        log.info("User added: {}", user);
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new ValidationException("User with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new ValidationException("User with id " + user.getId() + " does not exist");
        }
        users.remove(user.getId());
        log.info("User removed: {}", user);
    }

    @Override
    public void checkEmail(User user) {
        if (userEmails.contains(user.getEmail())) {
            log.error("User email already exists");
            throw new ValidationException("User with such email already exists");
        }
    }

    @Override
    public void checkBirthday(User user) {
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday date is in the future");
            throw new ValidationException("User's birthday is in the future: " + user.getBirthday());
        }
    }

    @Override
    public void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name is empty. Setting login {} as user name", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    @Override
    public Map<Integer, User> getUsers() {
        log.info("Current number of users: {}", users.size());
        return users;
    }

    @Override
    public User getUser(Integer id) {
        if (!users.containsKey(id)) {
            log.error("User with id " + id + "does not exist");
            throw new ValidationException("User with id " + id + " does not exist");
        }
        return users.get(id);
    }

    @Override
    public Set<String> getUserEmails() {
        log.info("Current number of users emails: {}", userEmails.size());
        return userEmails;
    }

}
