package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Data
@Builder
public class Film {
    public static long countFilm;
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> likes;
    private List<Genre> genres;
    private Mpa mpa;
    private int rate;
}
