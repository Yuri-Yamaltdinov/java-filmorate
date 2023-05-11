package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component("UserDbStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

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
            throw new EntityNotFoundException(String.format("User with id %d not found.", user.getId()), "User");
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
            User user = new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email").trim(),
                    userRows.getString("login").trim(),
                    userRows.getString("name").trim(),
                    userRows.getDate("Birthday").toLocalDate());

            log.info("Found user: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.warn("User with id {} not found.", userId);
            throw new EntityNotFoundException("User id not found in storage", "User");
        }
    }

}
