package com.sample.service;

import com.sample.model.Theatre;
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
 * The type Theatre service.
 */
@Service
public class TheatreService {

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
        = LoggerFactory.getLogger(TheatreService.class);
    /**
     * JdbcClient.
     */
    private final JdbcClient jdbcClient;

    /**
     * Instantiates a new Theatre service.
     *
     * @param aJdbcClient the jdbc client
     */
    public TheatreService(final JdbcClient aJdbcClient) {
        this.jdbcClient = aJdbcClient;
    }

    private Theatre rowMapper(final ResultSet rs, final Integer rowNum)
            throws SQLException {
        return new Theatre(
                (UUID) rs.getObject(INDEX_1),
                rs.getString(INDEX_2),
                rs.getString(INDEX_3),
                rs.getObject(INDEX_4, LocalDateTime.class),
                rs.getString(INDEX_5),
                rs.getObject(INDEX_6, LocalDateTime.class),
                rs.getString(INDEX_7));
    }

    /**
     * Create theatre.
     *
     * @param userName the user name
     * @param locale   the locale
     * @param theatre    the theatre
     * @return the theatre
     */
    public Theatre create(final String userName, final Locale locale,
                        final Theatre theatre) {
        final UUID theatreId = UUID.randomUUID();

        String insertEventSQL = """
                INSERT INTO theatres(id, title,
                description, created_by)
                VALUES (?, ?, ?, ?)
                """;
        jdbcClient.sql(insertEventSQL)
                .param(INDEX_1, theatreId)
                .param(INDEX_2, theatre.title())
                .param(INDEX_3, theatre.description())
                .param(INDEX_4, userName)
                .update();

        if (locale != null) {
            createLocalizedEvent(theatreId, theatre, locale);
        }

        final Optional<Theatre> createdEvent
        = read(userName, locale, theatreId);
        logger.info("Theatre Created {}", theatreId);

        return createdEvent.get();
    }

    private int createLocalizedEvent(final UUID theatreId,
                        final Theatre theatre,
                         final Locale locale) {
        String insertLocalizedEventSQL = """
                INSERT INTO theatres_localized(
                theatre_id, locale, title, description)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcClient.sql(insertLocalizedEventSQL)
                .param(INDEX_1, theatreId)
                .param(INDEX_2, locale.getLanguage())
                .param(INDEX_3, theatre.title())
                .param(INDEX_4, theatre.description())
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
    public Optional<Theatre> read(final String userName, final Locale locale,
                                final UUID id) {
        final String query = locale == null
                ? """
                SELECT id, title, description, created_at,
                created_by, modified_at, modified_by
                FROM theatres
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
                FROM theatres e
                LEFT JOIN theatres_localized el ON e.id = el.theatre_id
                WHERE e.id = ?
                    AND (el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                        SELECT theatre_id
                        FROM theatres_localized
                        WHERE theatre_id = e.id AND locale = ?
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
     * Update theatre.
     *
     * @param id       the id
     * @param userName the user name
     * @param locale   the locale
     * @param theatre    the theatre
     * @return the theatre
     */
    public Theatre update(final UUID id, final String userName,
                        final Locale locale, final Theatre theatre) {
        logger.debug("Entering update for Theatre {}", id);
        final String query = locale == null
                ? """
                UPDATE theatres SET title=?, description=?,
                modified_by=? WHERE id=?
                """
                : """
                UPDATE theatres SET modified_by=? WHERE id=?
                """;
        int updatedRows = locale == null
                ? jdbcClient.sql(query)
                .param(INDEX_1, theatre.title())
                .param(INDEX_2, theatre.description())
                .param(INDEX_3, userName)
                .param(INDEX_4, id).update()
                : jdbcClient.sql(query)
                .param(INDEX_1, userName)
                .param(INDEX_2, id).update();

        if (updatedRows == 0) {
            logger.error("Update not found {}", id);
            throw new IllegalArgumentException("Theatre not found");
        } else if (locale != null) {
            updatedRows = jdbcClient.sql("""
                            UPDATE theatres_localized SET title=?, locale=?,
                            description=?
                            WHERE theatre_id=? AND locale=?
                            """)
                    .param(INDEX_1, theatre.title())
                    .param(INDEX_2, locale.getLanguage())
                    .param(INDEX_3, theatre.description())
                    .param(INDEX_4, id)
                    .param(INDEX_5, locale.getLanguage())
                    .update();

            if (updatedRows == 0) {

                createLocalizedEvent(id, theatre, locale);
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
        return jdbcClient.sql("DELETE FROM theatres WHERE id = ?")
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
    public List<Theatre> list(final String userName, final Locale locale) {
        final String query = locale == null
                ? """
                SELECT id, title, description, created_at,
                created_by, modified_at, modified_by
                FROM theatres
                """
                : """
                SELECT DISTINCT e.id,
                    CASE WHEN el.locale = ? THEN el.title
                    ELSE e.title END AS title,
                    CASE WHEN el.locale = ? THEN el.description
                    ELSE e.description END AS description,
                    e.created_at, e.created_by,
                    e.modified_at, e.modified_by
                FROM theatres e
                LEFT JOIN theatres_localized el ON e.id = el.theatre_id
                WHERE el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                    SELECT theatre_id
                    FROM theatres_localized
                    WHERE theatre_id = e.id AND locale = ?
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
     * Delete all the theatres related data.
     */
    public void delete() {
        jdbcClient.sql("DELETE FROM theatres_localized").update();
        jdbcClient.sql("DELETE FROM theatres").update();
    }
}


