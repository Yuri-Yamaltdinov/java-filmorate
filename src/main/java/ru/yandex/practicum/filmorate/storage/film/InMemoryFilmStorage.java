package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @Override
    public void addFilm(Film film) {
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Film saved: {}", film);
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new ValidationException("Film with id " + film.getId() + " does not exist");
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
    }

    @Override
    public void deleteFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new ValidationException("Film with id " + film.getId() + " does not exist");
        }
        films.remove(film.getId());
        log.info("Film removed: {}", film);
    }

    @Override
    public void checkReleaseDate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Film release date cannot be earlier than min release date");
            throw new ValidationException("Film release date cannot be earlier than: {}" + MIN_RELEASE_DATE);
        }
    }

    @Override
    public Map<Integer, Film> getFilms() {
        log.info("Currently {} films saved", films.size());
        return films;
    }

    @Override
    public Film getFilm(Integer id) {
        if (!films.containsKey(id)) {
            log.error("Film with id " + id + "does not exist");
            throw new ValidationException("Film with id " + id + " does not exist");
        }
        return films.get(id);
    }
}
