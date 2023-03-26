package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
	Film film;
	User user;
	FilmController filmController;
	UserController userController;

	@BeforeEach
	public void beforeEach() {
		filmController = new FilmController();
		userController = new UserController();
	}

/*	@Test
	void postUserWithIncorrectEmail() {
		user = User.builder()
				.email("mail.ru")
				.name("Username")
				.login("UserLogin")
				.birthday(LocalDate.of(1999, 1, 1))
				.build();

		final ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.postUser(user)
		);
		assertEquals("User email has incorrect format", exception.getMessage());
	}*/

/*	@Test
	void putUserWithIncorrectEmail() {
		user = User.builder()
				.email("mail@mail.ru")
				.name("Username")
				.login("UserLogin")
				.birthday(LocalDate.of(1999, 1, 1))
				.build();
		userController.postUser(user);

		User updatedUser = User.builder()
				.id(1)
				.email("mail.ru")
				.name("Username")
				.login("UserLogin")
				.birthday(LocalDate.of(1999, 1, 1))
				.build();

		final ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.putUser(updatedUser)
		);
		assertEquals("User email has incorrect format", exception.getMessage());
	}*/

/*	@Test
	void postFilmWithoutReleaseDate() {
		film = Film.builder()
				.name("Name")
				.description("Description")
				.duration(180)
				.build();

		final ValidationException exception = assertThrows(
				ValidationException.class,
				() -> filmController.postFilm(film)
		);
		assertEquals("Film release date does not exist", exception.getMessage());
	}*/
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
		final ValidationException exception = assertThrows(
				ValidationException.class,
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
		final ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.putUser(user)
		);
		assertEquals("User with id 111 does not exist", exception.getMessage());
	}
/*	@Test
	void putFilmWithoutReleaseDate() {
		film = Film.builder()
				.name("Name")
				.description("Description")
				.duration(180)
				.build();

		final ValidationException exception = assertThrows(
				ValidationException.class,
				() -> filmController.putFilm(film)
		);
		assertEquals("Film release date does not exist", exception.getMessage());
	}*/
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
}
