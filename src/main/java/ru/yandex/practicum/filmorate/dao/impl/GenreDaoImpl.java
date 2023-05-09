package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre findById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);
        if (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(Integer.parseInt(genreRows.getString("genre_id")))
                    .name(genreRows.getString("genre_name").trim())
                    .build();

            log.info("Genre found: {} {}", genre.getId(), genre.getName());

            return genre;
        } else {
            log.info("Genre with id {} not found.", id);
            throw new GenreNotFoundException(String.format("Genre with id %d not found.", id));
        }
    }

    @Override
    public List<Genre> findAll() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres");
        List<Genre> genres = new ArrayList<>();

        while (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(Integer.parseInt(genreRows.getString("genre_id")))
                    .name(genreRows.getString("genre_name").trim())
                    .build();
            genres.add(genre);
        }
        log.info("Got the list of {} genres", genres.size());
        return genres;
    }
}
