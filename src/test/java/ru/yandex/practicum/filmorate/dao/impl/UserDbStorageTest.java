package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    @Order(1)
    void testCreateAndFindById() {
        User userTest = User.builder()
                .name("user")
                .birthday(LocalDate.of(1999, 9, 9))
                .email("user@user.user")
                .login("user")
                .build();
        userStorage.create(userTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    @Order(2)
    void testUpdate() {
        User userTestUpdate = User.builder()
                .id(1)
                .name("update user")
                .birthday(LocalDate.of(1997, 7, 7))
                .email("updateuser@user.user")
                .login("update login")
                .build();
        userStorage.update(userTestUpdate);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "update user")
                                .hasFieldOrPropertyWithValue("email", "updateuser@user.user")
                                .hasFieldOrPropertyWithValue("login", "update login")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1997, 7, 7))
                );
    }

    @Test
    @Order(4)
    void testDelete() {
        User userTest = User.builder()
                .id(1)
                .name("user")
                .birthday(LocalDate.of(1999, 9, 9))
                .email("user@user.user")
                .login("user")
                .build();
        userStorage.delete(userTest);
        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> userStorage.findById(1));
        assertEquals("User id not found in storage", ex.getMessage());
    }

    @Test
    @Order(3)
    void findAll() {
        User userTest2 = User.builder()
                .name("user test 2")
                .birthday(LocalDate.of(1992, 2, 2))
                .email("usertest2@user.user")
                .login("user test 2")
                .build();
        User userTest3 = User.builder()
                .name("user test 3")
                .birthday(LocalDate.of(1993, 3, 3))
                .email("usertest3@user.user")
                .login("user test 3")
                .build();
        userStorage.create(userTest2);
        userStorage.create(userTest3);
        Optional<List<User>> optionalUserList = Optional.ofNullable(userStorage.findAll());
        assertThat(optionalUserList)
                .isPresent()
                .hasValueSatisfying(users ->
                        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "update user")
                                .hasFieldOrPropertyWithValue("email", "updateuser@user.user")
                                .hasFieldOrPropertyWithValue("login", "update login")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1997, 7, 7))
                )
                .hasValueSatisfying(users ->
                        assertThat(users.get(1)).hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("name", "user test 2")
                                .hasFieldOrPropertyWithValue("email", "usertest2@user.user")
                                .hasFieldOrPropertyWithValue("login", "user test 2")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1992, 2, 2))
                )
                .hasValueSatisfying(users ->
                        assertThat(users.get(2)).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("name", "user test 3")
                                .hasFieldOrPropertyWithValue("email", "usertest3@user.user")
                                .hasFieldOrPropertyWithValue("login", "user test 3")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1993, 3, 3))
                )
                .hasValueSatisfying(users ->
                        assertThat(users.size()).isEqualTo(3)
                );
    }

}