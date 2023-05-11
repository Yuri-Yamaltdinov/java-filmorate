package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikesService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final LikesService likesService;

    @Autowired
    public FilmController(FilmService filmService, LikesService likesService) {
        this.filmService = filmService;
        this.likesService = likesService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET request received.");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Integer id) {
        log.info("GET request received: /{}", id);
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        log.info("GET request received: /popular?count={}", count);
        return likesService.getTopFilms(count);
    }

    @PostMapping
    public Film create(@NotNull @Valid @RequestBody Film film) {
        log.info("POST request received: {}", film);
        filmService.create(film);
        return film;
    }

    @PutMapping
    public Film update(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        filmService.update(film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("PUT request received: /{}/like/{}", id, userId);
        likesService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("DELETE request received: /{}/like/{}", id, userId);
        likesService.removeLike(id, userId);
    }

}
