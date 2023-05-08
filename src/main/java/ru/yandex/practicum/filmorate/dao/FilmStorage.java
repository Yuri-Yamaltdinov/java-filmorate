package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    Film create(Film film);

    Film update(Film film);

    void delete(Film film);

    List<Film> findAll();

    Film findById(Integer id);
}
