package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addUser(User user) {
        userStorage.checkEmail(user);
        userStorage.checkBirthday(user);
        userStorage.checkName(user);
        userStorage.addUser(user);
    }

    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("User id does not exist");
        }
        userStorage.checkBirthday(user);
        userStorage.checkName(user);
        userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers().values();
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id);
    }

    //добавление в друзья, удаление из друзей, вывод списка общих друзей.
    public User addFriends(Integer userId, Integer friendId) {
        return userStorage.addFriends(userId, friendId);
    }

    public User removeFromFriends(Integer userId, Integer friendId) {
        return userStorage.removeFromFriends(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        Set<Integer> userFriendsId = userStorage.getFriendsList(userId);
        Collection<User> friendsList = new ArrayList<>(Collections.emptyList());
        for (Integer id : userFriendsId) {
            friendsList.add(userStorage.getUser(id));
        }
        return friendsList;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        Set<Integer> userFriends = userStorage.getFriendsList(userId);
        Set<Integer> friendFriends = userStorage.getFriendsList(friendId);
        Set<Integer> commonFriendsIdList = userFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toSet());
        Collection<User> commonFriendsList = new ArrayList<>(Collections.emptyList());
        for (Integer commonFriendId : commonFriendsIdList) {
            commonFriendsList.add(userStorage.getUser(commonFriendId));
        }
        return commonFriendsList;
    }
}
