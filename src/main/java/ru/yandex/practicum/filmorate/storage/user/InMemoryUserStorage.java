package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
            throw new UserNotFoundException("User with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id " + user.getId() + "does not exist");
            throw new UserNotFoundException("User with id " + user.getId() + " does not exist");
        }
        users.remove(user.getId());
        log.info("User removed: {}", user);
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
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        return users.get(id);
    }

    @Override
    public Set<String> getUserEmails() {
        log.info("Current number of users emails: {}", userEmails.size());
        return userEmails;
    }

    @Override
    public User addFriends(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        users.put(friend.getId(), friend);
        log.info("Friend updated: {}", friend);
        return user;
    }

    @Override
    public User removeFromFriends(Integer userId, Integer friendId) {
        getUser(userId).removeFriend(friendId);
        getUser(friendId).removeFriend(userId);
        return getUser(userId);
    }

    @Override
    public Set<Integer> getFriendsList(Integer userId) {
        User user = getUser(userId);
        if ((user.getFriends() == null) || (user.getFriends().isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        return user.getFriends();
    }

}
