package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;

public interface LikesDao {

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    Collection<Integer> findTopFilms(Integer count);

}
