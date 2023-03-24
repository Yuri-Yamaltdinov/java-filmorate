package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @NotNull
    @NotBlank
    @NotEmpty
    @Email
    private String email;
    @NonNull
    @NotBlank
    @NotEmpty
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
