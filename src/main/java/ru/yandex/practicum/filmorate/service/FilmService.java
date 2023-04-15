package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addFilm(Film film) {
        filmStorage.checkReleaseDate(film);
        filmStorage.addFilm(film);
    }

    public void updateFilm(Film film) {
        filmStorage.checkReleaseDate(film);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms().values();
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    //добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
    public Film addLike(Integer filmId, Integer userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(Integer filmId, Integer userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

}
