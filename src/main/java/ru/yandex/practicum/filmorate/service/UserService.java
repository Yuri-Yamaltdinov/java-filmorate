package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
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
        userStorage.checkBirthday(user);
        userStorage.checkName(user);
        userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers().values();
    }

    //добавление в друзья, удаление из друзей, вывод списка общих друзей.
    public void addToFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user.getFriends().isEmpty() || (user.getFriends() == null)) {
            user.setFriends(Set.of(friendId));
        }
        Set<Integer> userFriends = user.getFriends();
        userFriends.add(friendId);
        user.setFriends(userFriends);

        if (friend.getFriends().isEmpty() || (friend.getFriends() == null)) {
            friend.setFriends(Set.of(userId));
        }
        Set<Integer> friendFriends = friend.getFriends();
        friendFriends.add(userId);
        friend.setFriends(friendFriends);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFromFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user.getFriends().isEmpty() || (user.getFriends() == null)) {
            log.error("User friends list is empty.");
            throw new ValidationException("User friends list is empty.");
        }
        Set<Integer> userFriends = user.getFriends();
        userFriends.remove(friendId);
        user.setFriends(userFriends);
        if (friend.getFriends().isEmpty() || (friend.getFriends() == null)) {
            log.error("Friend friends list is empty. Nothing to remove");
        } else {
            Set<Integer> friendFriends = friend.getFriends();
            friendFriends.remove(userId);
            friend.setFriends(friendFriends);
        }
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUser(userId);
        if (user.getFriends().isEmpty() || (user.getFriends() == null)) {
            log.error("User friends list is empty.");
            return null;
        }
        User friend = userStorage.getUser(friendId);
        if (!user.getFriends().isEmpty() || (user.getFriends() == null)) {
            log.error("User friends list is empty.");
            return null;
        }
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();
        Set<Integer> commonFriendsIdList = userFriends.stream().filter(friendFriends::contains).collect(Collectors.toSet());
        Collection<User> commonFriendsList = new ArrayList<>();
        for (Integer commonFriendId : commonFriendsIdList) {
            commonFriendsList.add(userStorage.getUser(commonFriendId));
        }
        return commonFriendsList;
    }
}
