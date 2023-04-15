package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private long duration;
    @Builder.Default
    private Set<Integer> likes = new HashSet<>();

    public void addLike(Integer userId) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(userId);
    }

    public void removeLike(Integer userId) {
        if (likes.isEmpty()) {
            throw new UserNotFoundException("Film's likes list is empty");
        }
        likes.remove(userId);
    }
}
