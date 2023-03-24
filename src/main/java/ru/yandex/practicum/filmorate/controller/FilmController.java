package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 0;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895,12,28);
    //добавление фильма;
    @PostMapping
    public Film postFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("POST request received: {}", film);
        checkReleaseDate(film.getReleaseDate());
        id++;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Сохранен фильм {}", film);
        return film;
    }
    //обновление фильма;
    @PutMapping
    public Film putFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        checkReleaseDate(film.getReleaseDate());
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new ValidationException("Film with id " + film.getId() + " does not exist");
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return film;
    }
    //получение всех фильмов.
    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Currently {} films saved", films.size());
        return films.values();
        //return new ArrayList<>(films.values());
    }

    private void checkReleaseDate (LocalDate releaseDate) {
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            log.error("Film release date cannot be earlier than min release date");
            throw new ValidationException("Film release date cannot be earlier than: {}" + MIN_RELEASE_DATE);
        }
    }

}
