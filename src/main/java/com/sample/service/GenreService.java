package com.sample.service;

import com.sample.model.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Genre service.
 */
@Service
public class GenreService {

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
     * Logger.
     */
    private final Logger logger
        = LoggerFactory.getLogger(GenreService.class);
    /**
     * JdbcClient.
     */
    private final JdbcClient jdbcClient;

    /**
     * Instantiates a new Genre service.
     *
     * @param aJdbcClient the jdbc client
     */
    public GenreService(final JdbcClient aJdbcClient) {
        this.jdbcClient = aJdbcClient;
    }

    private Genre rowMapper(final ResultSet rs, final Integer rowNum)
            throws SQLException {
        return new Genre(
                (UUID) rs.getObject(INDEX_1),
                rs.getString(INDEX_2),
                rs.getString(INDEX_3),
                rs.getObject(INDEX_4, LocalDateTime.class),
                rs.getString(INDEX_5),
                rs.getObject(INDEX_6, LocalDateTime.class),
                rs.getString(INDEX_7));
    }

    /**
     * Create genre.
     *
     * @param userName the user name
     * @param locale   the locale
     * @param genre    the genre
     * @return the genre
     */
    public Genre create(final String userName, final Locale locale,
                        final Genre genre) {
        final UUID genreId = UUID.randomUUID();

        String insertEventSQL = """
                INSERT INTO genres(id, title,
                description, created_by)
                VALUES (?, ?, ?, ?)
                """;
        jdbcClient.sql(insertEventSQL)
                .param(INDEX_1, genreId)
                .param(INDEX_2, genre.title())
                .param(INDEX_3, genre.description())
                .param(INDEX_4, userName)
                .update();

        if (locale != null) {
            createLocalizedEvent(genreId, genre, locale);
        }

        final Optional<Genre> createdEvent
        = read(userName, locale, genreId);
        logger.info("Genre Created {}", genreId);

        return createdEvent.get();
    }

    private int createLocalizedEvent(final UUID genreId,
                        final Genre genre,
                         final Locale locale) {
        String insertLocalizedEventSQL = """
                INSERT INTO genres_localized(
                genre_id, locale, title, description)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcClient.sql(insertLocalizedEventSQL)
                .param(INDEX_1, genreId)
                .param(INDEX_2, locale.getLanguage())
                .param(INDEX_3, genre.title())
                .param(INDEX_4, genre.description())
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
    public Optional<Genre> read(final String userName, final Locale locale,
                                final UUID id) {
        final String query = locale == null
                ? """
                SELECT id, title, description, created_at,
                created_by, modified_at, modified_by
                FROM genres
                WHERE id = ?
                """
                : """
                SELECT DISTINCT e.id,
                    CASE WHEN el.locale = ? THEN el.title
                    ELSE e.title END AS title,
                    CASE WHEN el.locale = ? THEN el.description
                    ELSE e.description END AS description,
                    e.created_at, e.created_by,
                    e.modified_at, e.modified_by
                FROM genres e
                LEFT JOIN genres_localized el ON e.id = el.genre_id
                WHERE e.id = ?
                    AND (el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                        SELECT genre_id
                        FROM genres_localized
                        WHERE genre_id = e.id AND locale = ?
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
     * Update genre.
     *
     * @param id       the id
     * @param userName the user name
     * @param locale   the locale
     * @param genre    the genre
     * @return the genre
     */
    public Genre update(final UUID id, final String userName,
                        final Locale locale, final Genre genre) {
        logger.debug("Entering update for Genre {}", id);
        final String query = locale == null
                ? """
                UPDATE genres SET title=?, description=?,
                modified_by=? WHERE id=?
                """
                : """
                UPDATE genres SET modified_by=? WHERE id=?
                """;
        int updatedRows = locale == null
                ? jdbcClient.sql(query)
                .param(INDEX_1, genre.title())
                .param(INDEX_2, genre.description())
                .param(INDEX_3, userName)
                .param(INDEX_4, id).update()
                : jdbcClient.sql(query)
                .param(INDEX_1, userName)
                .param(INDEX_2, id).update();

        if (updatedRows == 0) {
            logger.error("Update not found {}", id);
            throw new IllegalArgumentException("Genre not found");
        } else if (locale != null) {
            updatedRows = jdbcClient.sql("""
                            UPDATE genres_localized SET title=?, locale=?,
                            description=?
                            WHERE genre_id=? AND locale=?
                            """)
                    .param(INDEX_1, genre.title())
                    .param(INDEX_2, locale.getLanguage())
                    .param(INDEX_3, genre.description())
                    .param(INDEX_4, id)
                    .param(INDEX_5, locale.getLanguage())
                    .update();

            if (updatedRows == 0) {

                createLocalizedEvent(id, genre, locale);
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
        return jdbcClient.sql("DELETE FROM genres WHERE id = ?")
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
    public List<Genre> list(final String userName, final Locale locale) {
        final String query = locale == null
                ? """
                SELECT id, title, description, created_at,
                created_by, modified_at, modified_by
                FROM genres
                """
                : """
                SELECT DISTINCT e.id,
                    CASE WHEN el.locale = ? THEN el.title
                    ELSE e.title END AS title,
                    CASE WHEN el.locale = ? THEN el.description
                    ELSE e.description END AS description,
                    e.created_at, e.created_by,
                    e.modified_at, e.modified_by
                FROM genres e
                LEFT JOIN genres_localized el ON e.id = el.genre_id
                WHERE el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                    SELECT genre_id
                    FROM genres_localized
                    WHERE genre_id = e.id AND locale = ?
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
     * Delete all the genres related data.
     */
    public void delete() {
        jdbcClient.sql("DELETE FROM genres_localized").update();
        jdbcClient.sql("DELETE FROM genres").update();
    }
}


