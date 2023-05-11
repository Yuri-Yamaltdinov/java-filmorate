package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;

public interface FriendshipsDao {

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Collection<Integer> findFriendsById(Integer userId);

    Collection<Integer> findCommonFriends(Integer userId, Integer friendId);
}
