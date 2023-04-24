package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements Storage<User> {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public void addOne(User user) {
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("User added: {}", user);
    }

    @Override
    public void updateOne(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new UserNotFoundException("User with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
    }

    @Override
    public void deleteOne(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new UserNotFoundException("User with id " + user.getId() + " does not exist");
        }
        users.remove(user.getId());
        log.info("User removed: {}", user);
    }

    @Override
    public Map<Integer, User> getAll() {
        log.info("Current number of users: {}", users.size());
        return users;
    }

    @Override
    public User getOne(Integer id) {
        if (!users.containsKey(id)) {
            log.error("User with id " + id + "does not exist");
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        return users.get(id);
    }
}
