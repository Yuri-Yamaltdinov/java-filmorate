package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_ratings where mpa_id = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(Integer.parseInt(mpaRows.getString("mpa_id")))
                    .name(mpaRows.getString("mpa_rating").trim())
                    .build();

            log.info("Rating found: {} {}", mpa.getId(), mpa.getName());

            return mpa;
        } else {
            log.info("Rating with id {} not found.", id);
            throw new EntityNotFoundException(String.format("Rating with id %d not found.", id), "Mpa");
        }
    }

    @Override
    public List<Mpa> findAll() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_ratings");
        List<Mpa> mpas = new ArrayList<>();

        while (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(Integer.parseInt(mpaRows.getString("mpa_id")))
                    .name(mpaRows.getString("mpa_rating").trim())
                    .build();
            mpas.add(mpa);
        }
        log.info("Got the list of {} ratings", mpas.size());
        return mpas;
    }
}