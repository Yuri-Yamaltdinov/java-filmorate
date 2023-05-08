package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Film create(Film film) {
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Film saved: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new FilmNotFoundException("Film with id " + film.getId() + " does not exist");
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return film;
    }

    @Override
    public void delete(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new FilmNotFoundException("Film with id " + film.getId() + " does not exist");
        }
        films.remove(film.getId());
        log.info("Film removed: {}", film);
    }

    @Override
    public List<Film> findAll() {
        log.info("Currently {} films saved", films.size());
        return (List<Film>) films.values();
    }

    @Override
    public Film findById(Integer id) {
        if (!films.containsKey(id)) {
            log.error("Film with id " + id + "does not exist");
            throw new FilmNotFoundException("Film with id " + id + " does not exist");
        }
        return films.get(id);
    }
}
