package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User create(User user) {
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("User added: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new UserNotFoundException("User with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;
    }

    @Override
    public void delete(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new UserNotFoundException("User with id " + user.getId() + " does not exist");
        }
        users.remove(user.getId());
        log.info("User removed: {}", user);
    }

    @Override
    public List<User> findAll() {
        log.info("Current number of users: {}", users.size());
        return (List<User>) users.values();
    }

    @Override
    public User findById(Integer id) {
        if (!users.containsKey(id)) {
            log.error("User with id " + id + "does not exist");
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        return users.get(id);
    }
}
