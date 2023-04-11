package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

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
        Film film = filmStorage.getFilm(filmId);
        if (!film.getLikes().isEmpty() || (!(film.getLikes() == null))) {
            film.setLikes(Set.of(userId));
        }
        Set<Integer> filmLikes = film.getLikes();
        filmLikes.add(userId);
        film.setLikes(filmLikes);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        if (!film.getLikes().isEmpty() || (!(film.getLikes() == null))) {
            log.error("Likes list is empty. Nothing to remove.");
            throw new ValidationException("Likes list is empty. Nothing to remove.");
        }
        Set<Integer> filmLikes = film.getLikes();
        filmLikes.remove(userId);
        film.setLikes(filmLikes);
        filmStorage.updateFilm(film);
        return film;
    }

    public Collection<Film> getTopFilms(Integer count) {
        Collection<Film> filmsList = filmStorage.getFilms().values();
        return filmsList.stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toSet());
    }

}
