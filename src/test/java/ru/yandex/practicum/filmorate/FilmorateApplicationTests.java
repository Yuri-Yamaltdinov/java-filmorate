package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
    Film film;
    User user;
    FilmController filmController;
    UserController userController;
    private FilmService filmService;
    private UserService userService;
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmController = new FilmController(new FilmService(filmStorage));
        userController = new UserController(new UserService(userStorage));
    }

    @Test
    void postCorrectUser() {
        user = User.builder()
                .email("user@mail.ru")
                .name("Username")
                .login("UserLogin")
                .birthday(LocalDate.of(1999, 1, 1))
                .build();

        User addedUser = userController.postUser(user);
        assertEquals(addedUser, userController.getUserById(addedUser.getId()));
    }

    @Test
    void postCorrectFilm() {
        film = Film.builder()
                .name("Name")
                .description("Description")
                .duration(180)
                .releaseDate(LocalDate.of(1980, 1, 1))
                .build();

        Film addedFilm = filmController.postFilm(film);
        assertEquals(addedFilm, filmController.getFilmById(addedFilm.getId()));
    }

    @Test
    void postFilmWithIncorrectReleaseDate() {
        film = Film.builder()
                .name("Name")
                .description("Description")
                .duration(180)
                .releaseDate(LocalDate.of(1880, 1, 1))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.postFilm(film)
        );
        assertEquals("Film release date cannot be earlier than: {}1895-12-28", exception.getMessage());
    }

    @Test
    void putFilmWithIncorrectId() {
        film = Film.builder()
                .name("Name")
                .description("Description")
                .duration(180)
                .releaseDate(LocalDate.of(1980, 1, 1))
                .build();
        filmController.postFilm(film);

        film.setId(111);
        final FilmNotFoundException exception = assertThrows(
                FilmNotFoundException.class,
                () -> filmController.putFilm(film)
        );
        assertEquals("Film with id 111 does not exist", exception.getMessage());
    }

    @Test
    void putUserWithIncorrectId() {
        user = User.builder()
                .email("user@mail.ru")
                .name("Username")
                .login("UserLogin")
                .birthday(LocalDate.of(1999, 1, 1))
                .build();

        userController.postUser(user);
        user.setId(111);
        user.setEmail("user111@mail.ru");
        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userController.putUser(user)
        );
        assertEquals("User with id 111 does not exist", exception.getMessage());
    }

    @Test
    void putFilmWithIncorrectReleaseDate() {
        film = Film.builder()
                .name("Name")
                .description("Description")
                .duration(180)
                .releaseDate(LocalDate.of(1880, 1, 1))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.putFilm(film)
        );
        assertEquals("Film release date cannot be earlier than: {}1895-12-28", exception.getMessage());
    }

    @Test
    void postUserWithExistingEmail() {
        user = User.builder()
                .email("user@mail.ru")
                .name("Username")
                .login("UserLogin")
                .birthday(LocalDate.of(1999, 1, 1))
                .build();
        User badUser = User.builder()
                .email("user@mail.ru")
                .name("Username1")
                .login("UserLogin1")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();

        userController.postUser(user);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.postUser(badUser)
        );
        assertEquals("User with such email already exists", exception.getMessage());
    }

    @Test
    void postUserWithIncorrectBirthday() {
        user = User.builder()
                .email("user@mail.ru")
                .name("Username")
                .login("UserLogin")
                .birthday(LocalDate.of(2111, 1, 1))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.postUser(user)
        );
        assertEquals("User's birthday is in the future: 2111-01-01", exception.getMessage());
    }

    @Test
    void getUserFriendsCommonEmpty() {
        user = User.builder()
                .email("user@mail.ru")
                .name("Username")
                .login("UserLogin")
                .birthday(LocalDate.of(1999, 1, 1))
                .build();

        User friend = User.builder()
                .email("friend@mail.ru")
                .name("Friendname")
                .login("FriendLogin")
                .birthday(LocalDate.of(1999, 2, 2))
                .build();

        User addedUser = userController.postUser(user);
        User addedFriend = userController.postUser(friend);
        assertEquals(addedUser, userController.getUserById(addedUser.getId()));
        assertEquals(addedFriend, userController.getUserById(addedFriend.getId()));

        Collection<User> commonFriends = userController.getCommonFriends(addedUser.getId(), addedFriend.getId());
        assertEquals(Collections.emptyList(), commonFriends);
    }

    @Test
    void putUser1AddFriend2() {
        user = User.builder()
                .email("user@mail.ru")
                .name("Username")
                .login("UserLogin")
                .birthday(LocalDate.of(1999, 1, 1))
                .build();

        User friend = User.builder()
                .email("friend@mail.ru")
                .name("Friendname")
                .login("FriendLogin")
                .birthday(LocalDate.of(1999, 2, 2))
                .build();

        User addedUser = userController.postUser(user);
        User addedFriend = userController.postUser(friend);
        assertEquals(addedUser, userController.getUserById(addedUser.getId()));
        assertEquals(addedFriend, userController.getUserById(addedFriend.getId()));

        userController.addFriend(1, 2);
        addedUser = userStorage.getUser(1);
        addedFriend = userStorage.getUser(2);
        Set<Integer> userFriends = addedUser.getFriends();
        Set<Integer> friendFriends = addedFriend.getFriends();
        assertEquals(Set.of(2), userFriends);
        assertEquals(Set.of(1), friendFriends);
    }
}
