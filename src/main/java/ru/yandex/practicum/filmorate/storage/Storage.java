package ru.yandex.practicum.filmorate.storage;

import java.util.Map;

public interface Storage<T> {
    void addOne(T element);

    void updateOne(T element);

    void deleteOne(T element);

    Map<Integer, T> getAll();

    T getOne(Integer id);
}
