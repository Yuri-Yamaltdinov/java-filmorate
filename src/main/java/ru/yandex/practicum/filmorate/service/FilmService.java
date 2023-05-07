package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addFilm(Film film) {
        checkReleaseDate(film);
        filmStorage.addOne(film);
    }

    public void updateFilm(Film film) {
        checkReleaseDate(film);
        filmStorage.updateOne(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAll().values();
    }

    public Film getFilm(Integer id) {
        return filmStorage.getOne(id);
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = getFilm(filmId);
        film.addLike(userId);
        updateFilm(film);
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = getFilm(filmId);
        film.removeLike(userId);
        updateFilm(film);
        return film;
    }

    public Collection<Film> getTopFilms(Integer count) {
        Collection<Film> filmsCollection = getFilms();
        if (count > filmsCollection.size()) {
            count = filmsCollection.size();
        }
        for (Film film : filmsCollection) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
        }
        return filmsCollection.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Film release date cannot be earlier than min release date");
            throw new ValidationException("Film release date cannot be earlier than: {}" + MIN_RELEASE_DATE);
        }
    }


}
