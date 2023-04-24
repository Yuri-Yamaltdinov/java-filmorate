package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements Storage<Film> {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public void addOne(Film film) {
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Film saved: {}", film);
    }

    @Override
    public void updateOne(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new FilmNotFoundException("Film with id " + film.getId() + " does not exist");
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
    }

    @Override
    public void deleteOne(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new FilmNotFoundException("Film with id " + film.getId() + " does not exist");
        }
        films.remove(film.getId());
        log.info("Film removed: {}", film);
    }

    @Override
    public Map<Integer, Film> getAll() {
        log.info("Currently {} films saved", films.size());
        return films;
    }

    @Override
    public Film getOne(Integer id) {
        if (!films.containsKey(id)) {
            log.error("Film with id " + id + "does not exist");
            throw new FilmNotFoundException("Film with id " + id + " does not exist");
        }
        return films.get(id);
    }
}
