package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

@Service
@Slf4j
public class LikesService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesDao likesDao;

    @Autowired
    public LikesService(FilmStorage filmStorage, UserStorage userStorage, LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesDao = likesDao;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likesDao.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likesDao.removeLike(filmId, userId);
    }

    public Collection<Film> getTopFilms(Integer count) {
        Collection<Integer> topFilmsIdList = likesDao.findTopFilms(count);
        if (topFilmsIdList.isEmpty()) {
            log.error("Top films list is empty");
            return Collections.emptySet();
        }
        Collection<Film> topFilmsList = new LinkedHashSet<>();
        if (count > topFilmsIdList.size()) {
            count = topFilmsIdList.size();
        }

        for (Integer id : topFilmsIdList) {
            topFilmsList.add(filmStorage.findById(id));
        }

        return topFilmsList;
    }
}
