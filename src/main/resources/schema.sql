-- Создание таблицы рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_id INTEGER PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_id INTEGER REFERENCES mpa_ratings(mpa_id)
);

-- Создание таблицы связей фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres(genre_id),
    PRIMARY KEY (film_id, genre_id)
);

-- Создание таблицы лайков
CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

-- Создание таблицы дружбы (односторонняя)
CREATE TABLE IF NOT EXISTS friendships (
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'pending',
    PRIMARY KEY (user_id, friend_id)
);

-- Индексы для оптимизации
CREATE INDEX IF NOT EXISTS idx_films_mpa ON films(mpa_id);
CREATE INDEX IF NOT EXISTS idx_film_genres_film ON film_genres(film_id);
CREATE INDEX IF NOT EXISTS idx_film_genres_genre ON film_genres(genre_id);
CREATE INDEX IF NOT EXISTS idx_likes_film ON likes(film_id);
CREATE INDEX IF NOT EXISTS idx_likes_user ON likes(user_id);
CREATE INDEX IF NOT EXISTS idx_friendships_user ON friendships(user_id);
CREATE INDEX IF NOT EXISTS idx_friendships_friend ON friendships(friend_id);