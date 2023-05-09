package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final Set<String> userEmails = new HashSet<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addUser(User user) {
        checkName(user);
        userStorage.create(user);
    }

    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("User id does not exist");
        }
        checkName(user);
        userStorage.update(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Friend id is equal to user id");
        }
        User user = findById(userId);
        User friend = findById(friendId);
        user.addFriend(friendId);
        updateUser(user);
        log.info("User updated: {}", user);
        return user;
    }

    public User removeFromFriends(Integer userId, Integer friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.removeFriend(friendId);
        updateUser(user);
        return user;
    }

    public Collection<User> getFriends(Integer userId) {
        User user = findById(userId);
        if ((user.getFriends() == null) || (user.getFriends().isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        Set<Integer> userFriendsId = user.getFriends();
        Collection<User> friendsList = new ArrayList<>();
        for (Integer id : userFriendsId) {
            friendsList.add(userStorage.findById(id));
        }
        return friendsList;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        User user = findById(userId);
        if ((user.getFriends() == null) || (user.getFriends().isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        Set<Integer> userFriends = user.getFriends();
        User friend = findById(friendId);
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
            commonFriendsList.add(userStorage.findById(commonFriendId));
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
