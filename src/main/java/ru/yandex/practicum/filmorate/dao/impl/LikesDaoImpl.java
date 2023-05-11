package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikesDaoImpl implements LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlQuery = "SELECT * FROM liked_films " +
                "WHERE film_id = ? " +
                "AND user_id = ?;";
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
        if (likeRows.next()) {
            log.info("Like from user {} already exists.", userId);
            throw new ValidationException(String.format("Like from user %s already exists.", userId));
        }

        sqlQuery = "MERGE INTO liked_films (film_id, user_id) " +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("User {} liked film {}.", userId, filmId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String sqlQuery = "DELETE FROM liked_films " +
                "WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("User's {} like removed from film {}.", userId, filmId);
    }

    @Override
    public Collection<Integer> findTopFilms(Integer count) {
        Collection<Integer> filmsCollectionId = new LinkedHashSet<>();
        String sqlQuery = "SELECT f.film_id, COUNT(lf.user_id) AS total_likes " +
                "FROM films AS f " +
                "LEFT JOIN liked_films AS lf ON f.film_id = lf.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY total_likes DESC " +
                "LIMIT ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, count);
        while (filmRows.next()) {
            filmsCollectionId.add(filmRows.getInt("film_id"));
        }
        return filmsCollectionId;
    }
}
