package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface Storage<T> {
    T create(T element);

    T update(T element);

    void delete(T element);

    List<T> findAll();

    T findById(Integer id);
}
