package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Map;

public interface FilmStorage extends Storage<Film> {
    void addOne(Film film);

    void updateOne(Film film);

    void deleteOne(Film film);

    Map<Integer, Film> getAll();

    Film getOne(Integer id);
}
