package com.sample.service;

import com.sample.model.Tag;
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
 * The type Tag service.
 */
@Service
public class TagService {

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
        = LoggerFactory.getLogger(TagService.class);
    /**
     * JdbcClient.
     */
    private final JdbcClient jdbcClient;

    /**
     * Instantiates a new Tag service.
     *
     * @param aJdbcClient the jdbc client
     */
    public TagService(final JdbcClient aJdbcClient) {
        this.jdbcClient = aJdbcClient;
    }

    private Tag rowMapper(final ResultSet rs, final Integer rowNum)
            throws SQLException {
        return new Tag(
                (UUID) rs.getObject(INDEX_1),
                rs.getString(INDEX_2),
                rs.getString(INDEX_3),
                rs.getObject(INDEX_4, LocalDateTime.class),
                rs.getString(INDEX_5),
                rs.getObject(INDEX_6, LocalDateTime.class),
                rs.getString(INDEX_7));
    }

    /**
     * Create tag.
     *
     * @param userName the user name
     * @param locale   the locale
     * @param tag    the tag
     * @return the tag
     */
    public Tag create(final String userName, final Locale locale,
                        final Tag tag) {
        final UUID tagId = UUID.randomUUID();

        String insertEventSQL = """
                INSERT INTO tags(id, title,
                description, created_by)
                VALUES (?, ?, ?, ?)
                """;
        jdbcClient.sql(insertEventSQL)
                .param(INDEX_1, tagId)
                .param(INDEX_2, tag.title())
                .param(INDEX_3, tag.description())
                .param(INDEX_4, userName)
                .update();

        if (locale != null) {
            createLocalizedEvent(tagId, tag, locale);
        }

        final Optional<Tag> createdEvent
        = read(userName, locale, tagId);
        logger.info("Tag Created {}", tagId);

        return createdEvent.get();
    }

    private int createLocalizedEvent(final UUID tagId,
                        final Tag tag,
                         final Locale locale) {
        String insertLocalizedEventSQL = """
                INSERT INTO tags_localized(
                tag_id, locale, title, description)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcClient.sql(insertLocalizedEventSQL)
                .param(INDEX_1, tagId)
                .param(INDEX_2, locale.getLanguage())
                .param(INDEX_3, tag.title())
                .param(INDEX_4, tag.description())
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
    public Optional<Tag> read(final String userName, final Locale locale,
                                final UUID id) {
        final String query = locale == null
                ? """
                SELECT id, title, description, created_at,
                created_by, modified_at, modified_by
                FROM tags
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
                FROM tags e
                LEFT JOIN tags_localized el ON e.id = el.tag_id
                WHERE e.id = ?
                    AND (el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                        SELECT tag_id
                        FROM tags_localized
                        WHERE tag_id = e.id AND locale = ?
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
     * Update tag.
     *
     * @param id       the id
     * @param userName the user name
     * @param locale   the locale
     * @param tag    the tag
     * @return the tag
     */
    public Tag update(final UUID id, final String userName,
                        final Locale locale, final Tag tag) {
        logger.debug("Entering update for Tag {}", id);
        final String query = locale == null
                ? """
                UPDATE tags SET title=?, description=?,
                modified_by=? WHERE id=?
                """
                : """
                UPDATE tags SET modified_by=? WHERE id=?
                """;
        int updatedRows = locale == null
                ? jdbcClient.sql(query)
                .param(INDEX_1, tag.title())
                .param(INDEX_2, tag.description())
                .param(INDEX_3, userName)
                .param(INDEX_4, id).update()
                : jdbcClient.sql(query)
                .param(INDEX_1, userName)
                .param(INDEX_2, id).update();

        if (updatedRows == 0) {
            logger.error("Update not found {}", id);
            throw new IllegalArgumentException("Tag not found");
        } else if (locale != null) {
            updatedRows = jdbcClient.sql("""
                            UPDATE tags_localized SET title=?, locale=?,
                            description=?
                            WHERE tag_id=? AND locale=?
                            """)
                    .param(INDEX_1, tag.title())
                    .param(INDEX_2, locale.getLanguage())
                    .param(INDEX_3, tag.description())
                    .param(INDEX_4, id)
                    .param(INDEX_5, locale.getLanguage())
                    .update();

            if (updatedRows == 0) {

                createLocalizedEvent(id, tag, locale);
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
        return jdbcClient.sql("DELETE FROM tags WHERE id = ?")
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
    public List<Tag> list(final String userName, final Locale locale) {
        final String query = locale == null
                ? """
                SELECT id, title, description, created_at,
                created_by, modified_at, modified_by
                FROM tags
                """
                : """
                SELECT DISTINCT e.id,
                    CASE WHEN el.locale = ? THEN el.title
                    ELSE e.title END AS title,
                    CASE WHEN el.locale = ? THEN el.description
                    ELSE e.description END AS description,
                    e.created_at, e.created_by,
                    e.modified_at, e.modified_by
                FROM tags e
                LEFT JOIN tags_localized el ON e.id = el.tag_id
                WHERE el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                    SELECT tag_id
                    FROM tags_localized
                    WHERE tag_id = e.id AND locale = ?
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
     * Delete all the tags related data.
     */
    public void delete() {
        jdbcClient.sql("DELETE FROM tags_localized").update();
        jdbcClient.sql("DELETE FROM tags").update();
    }
}


