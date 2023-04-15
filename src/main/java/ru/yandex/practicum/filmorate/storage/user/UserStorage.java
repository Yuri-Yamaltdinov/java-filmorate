package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Set;

public interface UserStorage {
    void addUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    void checkBirthday(User user);

    void checkEmail(User user);

    void checkName(User user);

    Map<Integer, User> getUsers();

    User getUser(Integer id);

    Set<String> getUserEmails();

    User addFriends(Integer userId, Integer friendId);

    User removeFromFriends(Integer userId, Integer friendId);

    Set<Integer> getFriendsList(Integer userId);

}
