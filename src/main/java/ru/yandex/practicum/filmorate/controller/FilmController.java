package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film postFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("POST request received: {}", film);
        filmService.addFilm(film);
        return film;
    }

    @PutMapping
    public Film putFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        filmService.updateFilm(film);
        return film;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

}
