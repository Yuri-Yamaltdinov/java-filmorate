package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("UserDbStorage")
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId((int) simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("User created: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());

        if (userRows.next()) {
            Set<Integer> friends = user.getFriends();
            user.setFriends(updateFriends(friends, user.getId()));

            sqlQuery = "UPDATE users SET " +
                    "email= ?, " +
                    "login = ?, " +
                    "name = ?, " +
                    "birthday = ? " +
                    "where user_id = ?;";

            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());

            log.info("User updated: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.warn("User with id {} not found.", user.getId());
            throw new UserNotFoundException(String.format("User with id %d not found.", user.getId()));
        }

    }

    @Override
    public void delete(User user) {
        String sqlQuery = "DELETE FROM users " +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, user.getId());
        log.info("User {} deleted.", user.getId());
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM users");

        while (filmsRows.next()) {
            users.add(this.findById(Integer.parseInt(Objects.requireNonNull(filmsRows.getString("user_id")))));
        }

        log.info("Найдено пользователей: {}.", users.size());
        return users;
    }

    @Override
    public User findById(Integer userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", userId);
        if (userRows.next()) {
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(
                    "SELECT friend_id " +
                            "FROM friendships " +
                            "WHERE (user_id = ?);", userId);

            Set<Integer> friends = new HashSet<>();
            while (friendsRows.next()) {
                friends.add(friendsRows.getInt("friend_id"));
            }

            User user = new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email").trim(),
                    userRows.getString("login").trim(),
                    userRows.getString("name").trim(),
                    userRows.getDate("Birthday").toLocalDate(),
                    friends);

            log.info("Found user: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.warn("User with id {} not found.", userId);
            throw new UserNotFoundException("User id not found in storage");
        }
    }

    private Set<Integer> updateFriends(Set<Integer> friends, Integer userId) {
        String sqlQuery = "DELETE FROM friendships " +
                "WHERE user_id = ?;";

        jdbcTemplate.update(sqlQuery, userId);

        if (friends == null || friends.isEmpty()) {
            return friends;
        }

        for (Integer friendId : friends) {
            sqlQuery = "SELECT * FROM friendships " +
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
                sqlQuery = "INSERT INTO friendships (user_id, friend_id, confirmation) " +
                        "VALUES (?, ?, ?);";
                jdbcTemplate.update(sqlQuery, userId, friendId, true);

                log.info("Confirmed friendship request from user {} to user {}.", friendId, userId);
                return friends;
            }
            sqlQuery = "MERGE INTO friendships (user_id, friend_id) " +
                    "VALUES (?, ?);";
            jdbcTemplate.update(sqlQuery, userId, friendId);
            log.info("User {} has send friendship request to user {}.", userId, friendId);
        }
        return friends;
    }

}
