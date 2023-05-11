package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final MpaDao mpaDao;
    @Autowired
    private final GenreDao genreDao;

    @Override
    public Film create(Film film) {
        int mpaId = film.getMpa().getId();
        film.setMpa(mpaDao.findById(mpaId));

        List<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDao.findById(genre.getId());
            }
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId((int) simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());

        film.setGenres(updateGenres(genres, film.getId()));

        log.info("Film created: {} {}.", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, film.getId());

        if (filmRows.next()) {
            sqlQuery = "UPDATE films SET " +
                    "title= ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "mpa_rating_id = ? " +
                    "WHERE film_id = ?;";

            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            List<Genre> genres = film.getGenres();
            film.setGenres(updateGenres(genres, film.getId()));

            log.info("Film updated: {} {}", film.getId(), film.getName());

            return film;
        } else {
            log.warn("Film with id {} not found.", film.getId());
            throw new EntityNotFoundException(String.format("Film with id %d not found.", film.getId()), "Film");
        }
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM films " +
                "WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, film.getId());
        log.info("Film deleted: {} {}", film.getId(), film.getName());
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("SELECT film_id FROM films");

        while (filmsRows.next()) {
            films.add(findById(filmsRows.getInt("film_id")));
        }

        films.sort(Comparator.comparingInt(Film::getId));
        log.info("Films found: {}.", films.size());
        return films;
    }

    @Override
    public Film findById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        if (filmRows.next()) {
            sqlQuery = "SELECT genre_id FROM film_genres WHERE film_id = ?";
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

            List<Genre> genres = new ArrayList<>();
            while (genreRows.next()) {
                int genreId = genreRows.getInt("genre_id");
                genres.add(genreDao.findById(genreId));
            }

            sqlQuery = "SELECT user_id FROM liked_films WHERE film_id = ?";
            SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

            Film film = new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("title").trim(),
                    filmRows.getString("description").trim(),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    genres,
                    mpaDao.findById(filmRows.getInt("mpa_rating_id")));

            log.info("Film found: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.warn("Film with id {} not found.", filmId);
            throw new EntityNotFoundException("Film id not found in storage", "Film");
        }
    }

    private List<Genre> updateGenres(List<Genre> genres, Integer filmId) {
        List<Genre> genresResult = new ArrayList<>();
        String sqlQuery = "DELETE FROM film_genres " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery, filmId);

        if (genres != null && !genres.isEmpty()) {
            genres = genres.stream()
                    .distinct()
                    .collect(Collectors.toList());
            for (Genre genre : genres) {
                sqlQuery = "MERGE INTO film_genres (film_id, genre_id) " +
                        "VALUES (?, ?);";

                jdbcTemplate.update(sqlQuery, filmId, genre.getId());
                genre = genreDao.findById(genre.getId());
                genresResult.add(genre);
            }
        }
        return genresResult;
    }

}
