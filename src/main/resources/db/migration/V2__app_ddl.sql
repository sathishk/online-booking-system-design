CREATE TABLE movies (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    release_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
);


CREATE TABLE movies_localized (
    movie_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (movie_id) REFERENCES movies (id),
    PRIMARY KEY(movie_id, locale)
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
);


CREATE TABLE tags_localized (
    tag_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (tag_id) REFERENCES tags (id),
    PRIMARY KEY(tag_id, locale)
);


CREATE TABLE genres (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
);


CREATE TABLE genres_localized (
    genre_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (genre_id) REFERENCES genres (id),
    PRIMARY KEY(genre_id, locale)
);


CREATE TABLE theatres (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
);


CREATE TABLE theatres_localized (
    theatre_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (theatre_id) REFERENCES theatres (id),
    PRIMARY KEY(theatre_id, locale)
);
