package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    void addFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(Film film);

    void checkReleaseDate(Film film);

    Map<Integer, Film> getFilms();

    Film getFilm(Integer id);
}
