package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipsDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Service
@Slf4j
public class FriendshipsService {
    private final UserStorage userStorage;
    private final FriendshipsDao friendshipsDao;

    @Autowired
    public FriendshipsService(UserStorage userStorage, FriendshipsDao friendshipsDao) {
        this.userStorage = userStorage;
        this.friendshipsDao = friendshipsDao;
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Friend id is equal to user id");
        }
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendshipsDao.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendshipsDao.removeFriend(userId, friendId);
    }

    public Collection<User> findFriendsById(Integer userId) {
        User user = userStorage.findById(userId);
        Collection<Integer> friendsIdList = friendshipsDao.findFriendsById(userId);
        Collection<User> friendsList = new ArrayList<>();
        if ((friendsIdList.isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        for (Integer id : friendsIdList) {
            friendsList.add(userStorage.findById(id));
        }
        return friendsList;
    }

    public Collection<User> findCommonFriends(Integer userId, Integer friendId) {
        User user = userStorage.findById(userId);
        Collection<Integer> friendsIdList = friendshipsDao.findCommonFriends(userId, friendId);
        Collection<User> friendsList = new ArrayList<>();
        if ((friendsIdList.isEmpty())) {
            log.error("User friends list is empty.");
            return Collections.emptySet();
        }
        for (Integer id : friendsIdList) {
            friendsList.add(userStorage.findById(id));
        }
        return friendsList;
    }
}
