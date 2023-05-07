package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Map;

public interface UserStorage extends Storage<User> {
    void addOne(User user);

    void updateOne(User user);

    void deleteOne(User user);

    Map<Integer, User> getAll();

    User getOne(Integer id);
}
