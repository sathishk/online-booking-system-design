package com.sample.service;

import com.sample.model.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Movie service.
 */
@Service
public class MovieService {

    /**
     * Index.
     */
    private static final int INDEX_1 = 1;
    /**
     * Index.
     */
    private static final int INDEX_2 = 2;
    /**
     * Index.
     */
    private static final int INDEX_3 = 3;
    /**
     * Index.
     */
    private static final int INDEX_4 = 4;
    /**
     * Index.
     */
    private static final int INDEX_5 = 5;
    /**
     * Index.
     */
    private static final int INDEX_6 = 6;
    /**
     * Index.
     */
    private static final int INDEX_7 = 7;
    /**
     * Index.
     */
    private static final int INDEX_8 = 8;
    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(MovieService.class);
    /**
     * JdbcClient.
     */
    private final JdbcClient jdbcClient;

    /**
     * Instantiates a new Movie service.
     *
     * @param aJdbcClient the jdbc client
     */
    public MovieService(final JdbcClient aJdbcClient) {
        this.jdbcClient = aJdbcClient;
    }

    private Movie rowMapper(final ResultSet rs, final Integer rowNum)
            throws SQLException {
        return new Movie(
                (UUID) rs.getObject(INDEX_1),
                rs.getString(INDEX_2),
                rs.getString(INDEX_3),
                rs.getObject(INDEX_4, LocalDate.class),
                rs.getObject(INDEX_5, LocalDateTime.class),
                rs.getString(INDEX_6),
                rs.getObject(INDEX_7, LocalDateTime.class),
                rs.getString(INDEX_8));
    }

    /**
     * Create event.
     *
     * @param userName the user name
     * @param locale   the locale
     * @param event    the event
     * @return the event
     */
    public Movie create(final String userName, final Locale locale,
                        final Movie event) {
        final UUID eventId = UUID.randomUUID();

        String insertEventSQL = """
                INSERT INTO movies(id, title, release_date,
                description, created_by)
                VALUES (?, ?, ?, ?, ?)
                """;
        jdbcClient.sql(insertEventSQL)
                .param(INDEX_1, eventId)
                .param(INDEX_2, event.title())
                .param(INDEX_3, event.releaseDate())
                .param(INDEX_4, event.description())
                .param(INDEX_5, userName)
                .update();

        if (locale != null) {
            createLocalizedEvent(eventId, event, locale);
        }

        final Optional<Movie> createdEvent = read(userName, locale, eventId);
        logger.info("Movie Created {}", eventId);

        return createdEvent.get();
    }

    private int createLocalizedEvent(final UUID eventId, final Movie event,
                                     final Locale locale) {
        String insertLocalizedEventSQL = """
                INSERT INTO movies_localized(
                movie_id, locale, title, description)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcClient.sql(insertLocalizedEventSQL)
                .param(INDEX_1, eventId)
                .param(INDEX_2, locale.getLanguage())
                .param(INDEX_3, event.title())
                .param(INDEX_4, event.description())
                .update();
    }

    /**
     * Read optional.
     *
     * @param userName the user name
     * @param locale   the locale
     * @param id       the id
     * @return the optional
     */
    public Optional<Movie> read(final String userName, final Locale locale,
                                final UUID id) {
        final String query = locale == null
                ? """
                SELECT id, title, description, release_date, created_at,
                created_by, modified_at, modified_by
                FROM movies
                WHERE id = ?
                """
                : """
                SELECT DISTINCT e.id,
                    CASE WHEN el.locale = ? THEN el.title
                    ELSE e.title END AS title,
                    CASE WHEN el.locale = ? THEN el.description
                    ELSE e.description END AS description,
                    e.release_date, e.created_at, e.created_by,
                    e.modified_at, e.modified_by
                FROM movies e
                LEFT JOIN movies_localized el ON e.id = el.movie_id
                WHERE e.id = ?
                    AND (el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                        SELECT movie_id
                        FROM movies_localized
                        WHERE movie_id = e.id AND locale = ?
                    ))
                """;


            return locale == null ? jdbcClient
                    .sql(query).param(INDEX_1, id).query(this::rowMapper)
                    .optional()
                    : jdbcClient.sql(query)
                    .param(INDEX_1, locale.getLanguage())
                    .param(INDEX_2, locale.getLanguage())
                    .param(INDEX_3, id)
                    .param(INDEX_4, locale.getLanguage())
                    .param(INDEX_5, locale.getLanguage())
                    .query(this::rowMapper).optional();

    }

    /**
     * Update event.
     *
     * @param id       the id
     * @param userName the user name
     * @param locale   the locale
     * @param event    the event
     * @return the event
     */
    public Movie update(final UUID id, final String userName,
                        final Locale locale, final Movie event) {
        logger.debug("Entering update for Movie {}", id);
        final String query = locale == null
                ? """
                UPDATE movies SET title=?, release_date=?, description=?,
                modified_by=? WHERE id=?
                """
                : """
                UPDATE movies SET release_date=?, modified_by=? WHERE id=?
                """;
        int updatedRows = locale == null
                ? jdbcClient.sql(query)
                .param(INDEX_1, event.title())
                .param(INDEX_2, event.releaseDate())
                .param(INDEX_3, event.description())
                .param(INDEX_4, userName)
                .param(INDEX_5, id).update()
                : jdbcClient.sql(query)
                .param(INDEX_1, event.releaseDate())
                .param(INDEX_2, userName)
                .param(INDEX_3, id).update();

        if (updatedRows == 0) {
            logger.error("Update not found {}", id);
            throw new IllegalArgumentException("Movie not found");
        } else if (locale != null) {
            updatedRows = jdbcClient.sql("""
                            UPDATE movies_localized SET title=?, locale=?,
                            description=?
                            WHERE movie_id=? AND locale=?
                            """)
                    .param(INDEX_1, event.title())
                    .param(INDEX_2, locale.getLanguage())
                    .param(INDEX_3, event.description())
                    .param(INDEX_4, id)
                    .param(INDEX_5, locale.getLanguage())
                    .update();

            if (updatedRows == 0) {

                createLocalizedEvent(id, event, locale);
            }
        }
        return read(userName, locale, id).get();
    }

    /**
     * Delete boolean.
     *
     * @param userName the user name
     * @param id       the id
     * @return the boolean
     */
    public boolean delete(final String userName, final UUID id) {
        return jdbcClient.sql("DELETE FROM movies WHERE id = ?")
                .param(INDEX_1, id)
                .update() == 1;
    }

    /**
     * List list.
     *
     * @param userName the user name
     * @param locale   the locale
     * @return the list
     */
    public List<Movie> list(final String userName, final Locale locale) {
        final String query = locale == null
                ? """
                SELECT id, title, description, release_date, created_at,
                created_by, modified_at, modified_by
                FROM movies
                """
                : """
                SELECT DISTINCT e.id,
                    CASE WHEN el.locale = ? THEN el.title
                    ELSE e.title END AS title,
                    CASE WHEN el.locale = ? THEN el.description
                    ELSE e.description END AS description,
                    e.release_date, e.created_at, e.created_by,
                    e.modified_at, e.modified_by
                FROM movies e
                LEFT JOIN movies_localized el ON e.id = el.movie_id
                WHERE el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                    SELECT movie_id
                    FROM movies_localized
                    WHERE movie_id = e.id AND locale = ?
                )
                """;
        return locale == null
                ? jdbcClient.sql(query).query(this::rowMapper).list()
                : jdbcClient.sql(query)
                .param(INDEX_1, locale.getLanguage())
                .param(INDEX_2, locale.getLanguage())
                .param(INDEX_3, locale.getLanguage())
                .param(INDEX_4, locale.getLanguage())
                .query(this::rowMapper).list();
    }


    /**
     * Delete all the movies related data.
     */
    public void delete() {
        jdbcClient.sql("DELETE FROM movies_localized").update();
        jdbcClient.sql("DELETE FROM movies").update();
    }
}
