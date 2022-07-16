package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
public class User {
    public static long countUser;
    private long id;
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

}
