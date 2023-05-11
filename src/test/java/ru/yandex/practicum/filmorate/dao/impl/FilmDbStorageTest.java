package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    @Order(1)
    void testCreateAndFindById() {
        Film filmTest = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1998, 8, 8))
                .duration(100)
                .genres(List.of())
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .build();

        filmStorage.create(filmTest);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("mpa", Mpa.builder()
                                        .id(1)
                                        .name("G")
                                        .build())
                                .hasFieldOrPropertyWithValue("name", "film")
                                .hasFieldOrPropertyWithValue("description", "description")
                                .hasFieldOrPropertyWithValue("duration", 100)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1998, 8, 8))
                );
    }

    @Test
    @Order(2)
    void testUpdate() {
        Film filmTestUpdate = Film.builder()
                .id(1)
                .name("update film")
                .description("update description")
                .releaseDate(LocalDate.of(1997, 7, 7))
                .duration(90)
                .genres(List.of())
                .mpa(Mpa.builder()
                        .id(2)
                        .build())
                .build();
        filmStorage.update(filmTestUpdate);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("mpa", Mpa.builder()
                                        .id(2)
                                        .name("PG")
                                        .build())
                                .hasFieldOrPropertyWithValue("name", "update film")
                                .hasFieldOrPropertyWithValue("description", "update description")
                                .hasFieldOrPropertyWithValue("duration", 90)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1997, 7, 7))
                );
    }

    @Test
    @Order(4)
    void testDelete() {
        Film filmTest = Film.builder()
                .id(1)
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1998, 8, 8))
                .duration(100)
                .genres(List.of())
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .build();
        filmStorage.delete(filmTest);
        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> filmStorage.findById(1));
        assertEquals("Film id not found in storage", ex.getMessage());
    }

    @Test
    @Order(3)
    void testFindAll() {
        Film filmTest2 = Film.builder()
                .name("film test 2")
                .description("description test 2")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(180)
                .genres(List.of())
                .mpa(Mpa.builder()
                        .id(3)
                        .build())
                .build();
        Film filmTest3 = Film.builder()
                .name("film test 3")
                .description("description test 3")
                .releaseDate(LocalDate.of(2003, 3, 3))
                .duration(190)
                .genres(List.of(Genre.builder()
                        .id(1)
                        .build()))
                .mpa(Mpa.builder()
                        .id(4)
                        .build())
                .build();
        filmStorage.create(filmTest2);
        filmStorage.create(filmTest3);
        Optional<List<Film>> optionalFilmList = Optional.ofNullable(filmStorage.findAll());
        assertThat(optionalFilmList)
                .isPresent()
                .hasValueSatisfying(films ->
                        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("mpa", Mpa.builder()
                                        .id(2)
                                        .name("PG")
                                        .build())
                                .hasFieldOrPropertyWithValue("name", "update film")
                                .hasFieldOrPropertyWithValue("description", "update description")
                                .hasFieldOrPropertyWithValue("duration", 90)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1997, 7, 7))
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("mpa", Mpa.builder()
                                        .id(3)
                                        .name("PG-13")
                                        .build())
                                .hasFieldOrPropertyWithValue("name", "film test 2")
                                .hasFieldOrPropertyWithValue("description", "description test 2")
                                .hasFieldOrPropertyWithValue("duration", 180)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2002, 2, 2))
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(2)).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("mpa", Mpa.builder()
                                        .id(4)
                                        .name("R")
                                        .build())
                                .hasFieldOrPropertyWithValue("name", "film test 3")
                                .hasFieldOrPropertyWithValue("description", "description test 3")
                                .hasFieldOrPropertyWithValue("duration", 190)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2003, 3, 3))
                );
    }


}