package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final Storage<User> userStorage;
    private final Set<String> userEmails = new HashSet<>();

    @Autowired
    public UserService(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public void addUser(User user) {
        if (!(userEmails.add(user.getEmail()))) {
            log.error("User email already exists");
            throw new ValidationException("User with such email already exists");
        }
        checkName(user);
        userStorage.addOne(user);
    }

    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("User id does not exist");
        }
        checkName(user);
        userStorage.updateOne(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getAll().values();
    }

    public User getUser(Integer id) {
        return userStorage.getOne(id);
    }

    public User addFriends(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        updateUser(user);
        log.info("User updated: {}", user);
        updateUser(friend);
        log.info("Friend updated: {}", friend);
        return user;
    }

    public User removeFromFriends(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        updateUser(user);
        updateUser(friend);
        return user;
    }

    public Collection<User> getFriends(Integer userId) {
        User user = getUser(userId);
        if ((user.getFriends() == null) || (user.getFriends().isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        Set<Integer> userFriendsId = user.getFriends();
        Collection<User> friendsList = new ArrayList<>(Collections.emptyList());
        for (Integer id : userFriendsId) {
            friendsList.add(userStorage.getOne(id));
        }
        return friendsList;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        User user = getUser(userId);
        if ((user.getFriends() == null) || (user.getFriends().isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        Set<Integer> userFriends = user.getFriends();
        User friend = getUser(friendId);
        if ((friend.getFriends() == null) || (friend.getFriends().isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        Set<Integer> friendFriends = friend.getFriends();
        Set<Integer> commonFriendsIdList = userFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toSet());
        Collection<User> commonFriendsList = new ArrayList<>(Collections.emptyList());
        for (Integer commonFriendId : commonFriendsIdList) {
            commonFriendsList.add(userStorage.getOne(commonFriendId));
        }
        return commonFriendsList;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name is empty. Setting login {} as user name", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
