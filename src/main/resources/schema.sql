--таблица жанров
DROP TABLE IF EXISTS genres CASCADE;
CREATE TABLE genres (
	genre_id integer AUTO_INCREMENT PRIMARY KEY ,
	genre_name CHARACTER(40)
);

--таблица МРА-рейтингов
DROP TABLE IF EXISTS mpa_ratings CASCADE;
CREATE TABLE mpa_ratings (
	mpa_id int AUTO_INCREMENT PRIMARY KEY ,
	mpa_rating CHARACTER(40)
);

--таблица film_genres
DROP TABLE IF EXISTS film_genres CASCADE;
CREATE TABLE film_genres (
    film_id int,
    genre_id int,
    constraint pk_film_genres primary key (film_id, genre_id)
);

--таблица фильмов
DROP TABLE IF EXISTS films CASCADE;
CREATE TABLE films (
    film_id int AUTO_INCREMENT PRIMARY KEY ,
    title character(100),
    description character(200),
    release_date date,
    duration int,
    mpa_rating_id int
);

--таблица лайков фильмов
DROP TABLE IF EXISTS liked_films CASCADE;
CREATE TABLE liked_films (
    film_id int,
    user_id int,
    constraint pk_liked_films primary key (film_id, user_id)
);

--таблица пользователей
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    user_id int AUTO_INCREMENT PRIMARY KEY ,
    email character(100),
    login character(100),
    name character(100),
    birthday date
);

--таблица дружбы
DROP TABLE IF EXISTS friendships CASCADE;
CREATE TABLE friendships (
    user_id int,
    friend_id int,
    confirmation boolean DEFAULT FALSE,
    constraint pk_friendships primary key (user_id, friend_id)
);

--связывание внешних ключей
ALTER TABLE liked_films ADD CONSTRAINT fk_liked_films_film_id FOREIGN KEY(film_id)
REFERENCES films (film_id);

ALTER TABLE liked_films ADD CONSTRAINT fk_liked_films_user_id FOREIGN KEY(user_id)
REFERENCES users (user_id);

ALTER TABLE friendships ADD CONSTRAINT fk_friendships_user_id FOREIGN KEY(user_id)
REFERENCES users (user_id);

ALTER TABLE friendships ADD CONSTRAINT fk_friendships_friend_id FOREIGN KEY(friend_id)
REFERENCES users (user_id);

ALTER TABLE films ADD CONSTRAINT fk_films_mpa_rating_id FOREIGN KEY(mpa_rating_id)
REFERENCES mpa_ratings (mpa_id);

ALTER TABLE film_genres ADD CONSTRAINT fk_film_genres_film_id FOREIGN KEY(film_id)
REFERENCES films (film_id);

ALTER TABLE film_genres ADD CONSTRAINT fk_film_genres_genre_id FOREIGN KEY(genre_id)
REFERENCES genres (genre_id);