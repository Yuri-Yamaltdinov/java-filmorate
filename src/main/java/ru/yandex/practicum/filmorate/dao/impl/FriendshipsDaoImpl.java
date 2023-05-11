package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendshipsDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendshipsDaoImpl implements FriendshipsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT * FROM friendships " +
                "WHERE user_id = ? " +
                "AND friend_id = ?;";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        if (friendsRows.next()) {
            boolean isConfirm = friendsRows.getBoolean("CONFIRMATION");
            if (isConfirm) {
                log.info("Friendship with user {} already exists.", friendId);
                throw new ValidationException(String.format("Friendship with user %s already exists.", friendId));
            } else {
                log.warn("Friendship request sent to user {}. No confirmation.", friendId);
            }
        }
        friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, friendId, userId);
        if (friendsRows.next()) {
            sqlQuery = "UPDATE friendships " +
                    "SET confirmation = true " +
                    "WHERE user_id = ? AND friend_id = ?;";
            jdbcTemplate.update(sqlQuery, friendId, userId);
            sqlQuery = "MERGE INTO friendships (user_id, friend_id, confirmation) " +
                    "VALUES (?, ?, ?);";
            jdbcTemplate.update(sqlQuery, userId, friendId, true);

            log.info("Confirmed friendship request from user {} to user {}.", friendId, userId);
            return;
        }
        sqlQuery = "MERGE INTO friendships (user_id, friend_id) " +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("User {} has send friendship request to user {}.", userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM friendships " +
                "WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.info("User {} removed from friends of user {}.", friendId, userId);
        sqlQuery = "SELECT * FROM friendships " +
                "WHERE user_id = ? " +
                "AND friend_id = ?;";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, friendId, userId);
        if (friendsRows.next()) {
            sqlQuery = "UPDATE friendships " +
                    "SET confirmation = false " +
                    "WHERE user_id = ? AND friend_id = ?;";
            jdbcTemplate.update(sqlQuery, friendId, userId);
            log.info("Friendship confirmation status from user {} to user {} is false.", userId, friendId);
        }

    }

    @Override
    public Collection<Integer> findFriendsById(Integer userId) {
        Set<Integer> friendsId = new HashSet<>();
        String sqlQuery = "SELECT * FROM friendships " +
                "WHERE user_id = ?;";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        while (friendsRows.next()) {
            friendsId.add(friendsRows.getInt("friend_id"));
        }
        return friendsId;
    }

    @Override
    public Collection<Integer> findCommonFriends(Integer userId, Integer friendId) {
        Set<Integer> friendsId = new HashSet<>();
        String sqlQuery = "SELECT t1.friend_id from friendships as t1 " +
                "JOIN " +
                "(" +
                "SELECT user_id, friend_id from friendships\n" +
                ") " +
                "t2 on t1.friend_id = t2.friend_id " +
                "WHERE t1.user_id = ? and t2.user_id = ?";
        SqlRowSet commonFriendsRows = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        while (commonFriendsRows.next()) {
            friendsId.add(commonFriendsRows.getInt("friend_id"));
        }
        return friendsId;
    }
}
