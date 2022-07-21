package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	private final UserDbStorage us;
	private final FilmDbStorage fs;

	@Test
	void contextLoads() {
	}

	@Test
	void addUser(){
		User user = User.builder()
				.id(1)
				.birthday(LocalDate.parse("2007-12-03"))
				.email("testEmail")
				.login("testLogin")
				.name("testName")
				.friends(new HashSet<>())
				.build();

		us.create(user);
		Assertions.assertEquals(user, us.getUser(1));
	}

	@Test
	void updateUser(){
		User user = us.getUser(1);
		user.setName("updateUser");
		us.put(user);
		Assertions.assertEquals(user, us.getUser(1));
	}

	@Test
	void addFilm(){
		Film film = Film.builder()
				.id(1)
				.name("film")
				.description("des")
				.name("name")
				.releaseDate(LocalDate.parse("2007-12-03"))
				.mpa(Mpa.builder().id(1).name("G").build())
				.likes(new HashSet<>())
				.genres(new LinkedHashSet<>())
				.build();

		fs.create(film);
		Assertions.assertEquals(film, fs.getFilm(1));
	}

	@Test
	void updateFilm(){
		Film film = fs.getFilm(1);
		film.setName("testName");
		fs.put(film);
		Assertions.assertEquals(film, fs.getFilm(1));
	}


}
