package com.sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.lang.StringTemplate.RAW;

public class CodeGen {
    public static void main(String[] args) throws IOException {

        String name = args[0];
        String pluralName = getPluralName(name);

        generateDDL(name, pluralName);
        generateModel(name, pluralName);
        generateService(name, pluralName);
        generateServiceTest(name, pluralName);
        generateController(name, pluralName);
    }

    private static String getPluralName(String name) {
        if (name.endsWith("y")) {
            return name.substring(0,name.length()-1) + "ies";
        }
        return name + "s";
    }

    private static void generateController(String name,String pluralName) throws IOException {

        StringTemplate CONTROLLER_TEST_TEMPLATE = RAW
                ."""
package com.sample.controller;

import com.sample.model.\{name};
import com.sample.service.\{name}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/\{pluralName.toLowerCase()}")
@Tag(name = "\{pluralName}", description = "Resource to manage \{name}")
class \{name}APIController {

    /**
     * declare a \{name.toLowerCase()} service.
     */
    private final \{name}Service \{name.toLowerCase()}Service;

    \{name}APIController(final \{name}Service a\{name}Service) {
        this.\{name.toLowerCase()}Service = a\{name}Service;
    }

    /**
     * Create response entity.
     *
     * @param principal the principal
     * @param \{name.toLowerCase()}     the \{name.toLowerCase()} name
     * @param locale    the locale
     * @return the response entity
     */
    @Operation(summary = "Creates a new \{name.toLowerCase()}",
            description = "Can be called "
                    + "only by users with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
            description = "\{name.toLowerCase()} created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "\{name.toLowerCase()} is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public final ResponseEntity<\{name}> create(final Principal principal,
                @RequestHeader(name = "Accept-Language",
                required = false) final Locale locale,
                          @RequestBody final \{name} \{name.toLowerCase()}) {
        \{name} created = \{name.toLowerCase()}Service.create(principal.getName(),
                locale, \{name.toLowerCase()});
        return ResponseEntity.created(URI.create("/api/\{name.toLowerCase()}"
                        + created.id()))
                .body(created);
    }


    /**
     * Read a \{name.toLowerCase()}.
     *
     * @param id
     * @param principal
     * @param locale    the locale
     * @return a \{name.toLowerCase()}
     */
    @Operation(summary = "Get the \{name} with given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "getting \{name.toLowerCase()} successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})

    @GetMapping("/{id}")
    public final ResponseEntity<\{name}> read(@PathVariable final UUID id,
                            @RequestHeader(name = "Accept-Language",
                                          required = false) final Locale locale,
                                            final Principal principal) {
        return ResponseEntity.of(\{name.toLowerCase()}Service.read(principal.getName(),
                locale, id));
    }

    /**
     * Update a \{name}.
     *
     * @param id
     * @param principal
     * @param locale
     * @param \{name.toLowerCase()}
     * @return a \{name.toLowerCase()}
     */
    @Operation(summary = "Updates the \{name.toLowerCase()} by given id",
            description = "Can be called only by users "
                    + "with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "\{name.toLowerCase()} updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "\{name.toLowerCase()} is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})
    @PutMapping(value = "/{id}", produces = "application/json", consumes =
            "application/json")
    public final ResponseEntity<\{name}> update(@PathVariable final UUID id,
                                              final Principal
                                                principal,
                                      @RequestHeader(name = "Accept-Language",
                                        required = false) final Locale locale,
                                              @RequestBody final \{name}
                                                \{name.toLowerCase()}) {
        final \{name} updatedEvent =
                \{name.toLowerCase()}Service.update(id, principal.getName(),
                locale, \{name.toLowerCase()});
        return updatedEvent == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete a \{name}.
     *
     * @param id
     * @param principal
     * @return \{name.toLowerCase()}
     */
    @Operation(summary = "Deletes the \{name.toLowerCase()} by given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "\{name.toLowerCase()} deleted successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "\{name.toLowerCase()} not found")})
    @DeleteMapping("/{id}")
    public final ResponseEntity<Void> delete(@PathVariable final
                                       UUID id,
                                       final Principal principal) {
        return \{name.toLowerCase()}Service.delete(principal.getName(),
                id) ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * List the \{name}.
     *
     * @param principal
     * @param locale
     * @return list of \{name.toLowerCase()}
     */
    @Operation(summary = "lists the \{name.toLowerCase()}",
            description = " Can be invoked by auth users only",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Listing the \{name.toLowerCase()}"),
            @ApiResponse(responseCode = "204",
                    description = "\{name.toLowerCase()} are not available"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @GetMapping(produces = "application/json")
    public final ResponseEntity<List<\{name}>> list(final Principal
                                                    principal,
                                      @RequestHeader(name = "Accept-Language",
                                    required = false) final Locale locale) {
        final List<\{name}> \{name.toLowerCase()}List = \{name.toLowerCase()}Service.list(
                principal.getName(), locale);
        return \{name.toLowerCase()}List.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(\{name.toLowerCase()}List);
    }

}
                """;

        Files.write(
                Paths.get(RAW. "src/main/java/com/sample/controller/\{name}APIController.java".interpolate()),
                CONTROLLER_TEST_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.CREATE);
    }

    private static void generateServiceTest(String name,String pluralName) throws IOException {

        StringTemplate SERVICE_TEST_TEMPLATE = RAW
                ."""
package com.sample.service;

import com.sample.model.\{name};
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest
public class \{name}ServiceTest {

    public static final String STATE_BOARD_IN_ENGLISH = "State \{name}";
    public static final String STATE_BOARD_DESCRIPTION_IN_ENGLISH = "State \{name} Description";
    public static final String STATE_BOARD_TITLE_IN_FRENCH = "Conseil d'État";
    public static final String STATE_BOARD_DESCRIPTION_IN_FRENCH = "Description du conseil d'État";
    @Autowired
    private \{name}Service \{name.toLowerCase()}Service;

    /**
     * Before.
     *
     * @throws IOException the io exception
     */
    @BeforeEach
    void before() throws IOException {
        cleanUp();
    }

    /**
     * After.
     */
    @AfterEach
    void after() {
        cleanUp();
    }

    private void cleanUp() {
        \{name.toLowerCase()}Service.delete();
    }

    @Test
    void create() {
        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", null,
                anEvent());
        Assertions.assertTrue(\{name.toLowerCase()}Service.read("mani", null, \{name.toLowerCase()}.id()).isPresent(),
                "Created \{name}");
    }

    @Test
    void read() {
        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", null,
                anEvent());
        final UUID newEventId = \{name.toLowerCase()}.id();
        Assertions.assertTrue(\{name.toLowerCase()}Service.read("mani", null, newEventId).isPresent(),
                "\{name} Created");
    }

    @Test
    void update() {

        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", null,
                anEvent());
        final UUID newEventId = \{name.toLowerCase()}.id();
        \{name} newEvent = new \{name}(null, "\{name}", "A " +
                "\{name}",
                null, "tom", null, null);
        \{name} updatedEvent = \{name.toLowerCase()}Service
                .update(newEventId, "mani", null, newEvent);
        Assertions.assertEquals("\{name}", updatedEvent.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            \{name.toLowerCase()}Service
                    .update(UUID.randomUUID(), "mani", null, newEvent);
        });
    }

    @Test
    void delete() {

        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", null,
                anEvent());
        \{name.toLowerCase()}Service.delete("mani", \{name.toLowerCase()}.id());
        Assertions.assertFalse(\{name.toLowerCase()}Service.read("mani", null, \{name.toLowerCase()}.id()).isPresent(),
                "Deleted \{name}");

    }

    @Test
    void list() {

        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", null,
                anEvent());
        \{name} newEvent = new \{name}(null, "\{name} New", "A " +
                "\{name}",
                null, "tom", null, null);
        \{name.toLowerCase()}Service.create("mani", null,
                newEvent);
        List<\{name}> listof\{name.toLowerCase()} = \{name.toLowerCase()}Service.list("manikanta", null);
        Assertions.assertEquals(2, listof\{name.toLowerCase()}.size());

    }

    @Test
    void testLocalizationFromDefaultWithoutLocale() {
        // Create a \{name} without locale
        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", null,
                anEvent());

        testLocalization(\{name.toLowerCase()});

    }

    @Test
    void testLocalizationFromCreateWithLocale() {
        // Create a \{name} with locale
        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("mani", Locale.GERMAN,
                anEvent());

        testLocalization(\{name.toLowerCase()});

    }

    void testLocalization(\{name} \{name.toLowerCase()}) {

        // Update for China Language
        \{name.toLowerCase()}Service.update(\{name.toLowerCase()}.id(), "mani", Locale.FRENCH, anEvent(\{name.toLowerCase()},
                STATE_BOARD_TITLE_IN_FRENCH,
                STATE_BOARD_DESCRIPTION_IN_FRENCH));

        // Get for french Language
        \{name} createEvent = \{name.toLowerCase()}Service.read("mani", Locale.FRENCH,
                \{name.toLowerCase()}.id()).get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH, createEvent.description());

        final UUID id = createEvent.id();
        createEvent = \{name.toLowerCase()}Service.list("mani", Locale.FRENCH)
                .stream()
                .filter(\{name.toLowerCase()}1 -> \{name.toLowerCase()}1.id().equals(id))
                .findFirst().get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH,
                createEvent.description());

        // Get for France which does not have data
        createEvent = \{name.toLowerCase()}Service.read("mani", Locale.CHINESE,
                \{name.toLowerCase()}.id()).get();
        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

        createEvent = \{name.toLowerCase()}Service.list("mani", Locale.CHINESE)
                .stream()
                .filter(\{name.toLowerCase()}1 -> \{name.toLowerCase()}1.id().equals(id))
                .findFirst().get();

        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

    }

    /**
     * Gets \{name.toLowerCase()}.
     *
     * @return the \{name.toLowerCase()}
     */
    \{name} anEvent() {
        \{name} \{name.toLowerCase()} = new \{name}(null, STATE_BOARD_IN_ENGLISH,
                STATE_BOARD_DESCRIPTION_IN_ENGLISH,
                null, null,
                null, null);
        return \{name.toLowerCase()};
    }

    /**
     * Gets \{name.toLowerCase()} from reference \{name.toLowerCase()}.
     *
     * @return the \{name.toLowerCase()}
     */
    \{name} anEvent(final \{name} ref, final String title, final String description) {
        return new \{name}(ref.id(), title,
                description,
                ref.createdAt(), ref.createdBy(),
                ref.modifiedAt(), ref.modifiedBy());
    }
}
                """;
        Files.write(
                Paths.get(RAW. "src/test/java/com/sample/service/\{name}ServiceTest.java".interpolate()),
                SERVICE_TEST_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.CREATE);
    }
    private static void generateService(String name,String pluralName) throws IOException {
        StringTemplate SERVICE_TEMPLATE = RAW
                ."""
                package com.sample.service;

                import com.sample.model.\{name};
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
                 * The type \{name} service.
                 */
                @Service
                public class \{name}Service {

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
                        = LoggerFactory.getLogger(\{name}Service.class);
                    /**
                     * JdbcClient.
                     */
                    private final JdbcClient jdbcClient;

                    /**
                     * Instantiates a new \{name} service.
                     *
                     * @param aJdbcClient the jdbc client
                     */
                    public \{name}Service(final JdbcClient aJdbcClient) {
                        this.jdbcClient = aJdbcClient;
                    }

                    private \{name} rowMapper(final ResultSet rs, final Integer rowNum)
                            throws SQLException {
                        return new \{name}(
                                (UUID) rs.getObject(INDEX_1),
                                rs.getString(INDEX_2),
                                rs.getString(INDEX_3),
                                rs.getObject(INDEX_4, LocalDateTime.class),
                                rs.getString(INDEX_5),
                                rs.getObject(INDEX_6, LocalDateTime.class),
                                rs.getString(INDEX_7));
                    }

                    /**
                     * Create \{name.toLowerCase()}.
                     *
                     * @param userName the user name
                     * @param locale   the locale
                     * @param \{name.toLowerCase()}    the \{name.toLowerCase()}
                     * @return the \{name.toLowerCase()}
                     */
                    public \{name} create(final String userName, final Locale locale,
                                        final \{name} \{name.toLowerCase()}) {
                        final UUID \{name.toLowerCase()}Id = UUID.randomUUID();

                        String insertEventSQL = ""\"
                                INSERT INTO \{pluralName.toLowerCase()}(id, title,
                                description, created_by)
                                VALUES (?, ?, ?, ?)
                                ""\";
                        jdbcClient.sql(insertEventSQL)
                                .param(INDEX_1, \{name.toLowerCase()}Id)
                                .param(INDEX_2, \{name.toLowerCase()}.title())
                                .param(INDEX_3, \{name.toLowerCase()}.description())
                                .param(INDEX_4, userName)
                                .update();

                        if (locale != null) {
                            createLocalizedEvent(\{name.toLowerCase()}Id, \{name.toLowerCase()}, locale);
                        }

                        final Optional<\{name}> createdEvent
                        = read(userName, locale, \{name.toLowerCase()}Id);
                        logger.info("\{name} Created {}", \{name.toLowerCase()}Id);

                        return createdEvent.get();
                    }

                    private int createLocalizedEvent(final UUID \{name.toLowerCase()}Id,
                                        final \{name} \{name.toLowerCase()},
                                         final Locale locale) {
                        String insertLocalizedEventSQL = ""\"
                                INSERT INTO \{pluralName.toLowerCase()}_localized(
                                \{name.toLowerCase()}_id, locale, title, description)
                                VALUES (?, ?, ?, ?)
                                ""\";
                        return jdbcClient.sql(insertLocalizedEventSQL)
                                .param(INDEX_1, \{name.toLowerCase()}Id)
                                .param(INDEX_2, locale.getLanguage())
                                .param(INDEX_3, \{name.toLowerCase()}.title())
                                .param(INDEX_4, \{name.toLowerCase()}.description())
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
                    public Optional<\{name}> read(final String userName, final Locale locale,
                                                final UUID id) {
                        final String query = locale == null
                                ? ""\"
                                SELECT id, title, description, created_at,
                                created_by, modified_at, modified_by
                                FROM \{pluralName.toLowerCase()}
                                WHERE id = ?
                                ""\"
                                : ""\"
                                SELECT DISTINCT e.id,
                                    CASE WHEN el.locale = ? THEN el.title
                                    ELSE e.title END AS title,
                                    CASE WHEN el.locale = ? THEN el.description
                                    ELSE e.description END AS description,
                                    e.created_at, e.created_by,
                                    e.modified_at, e.modified_by
                                FROM \{pluralName.toLowerCase()} e
                                LEFT JOIN \{pluralName.toLowerCase()}_localized el ON e.id = el.\{name.toLowerCase()}_id
                                WHERE e.id = ?
                                    AND (el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                                        SELECT \{name.toLowerCase()}_id
                                        FROM \{pluralName.toLowerCase()}_localized
                                        WHERE \{name.toLowerCase()}_id = e.id AND locale = ?
                                    ))
                                ""\";


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
                     * Update \{name.toLowerCase()}.
                     *
                     * @param id       the id
                     * @param userName the user name
                     * @param locale   the locale
                     * @param \{name.toLowerCase()}    the \{name.toLowerCase()}
                     * @return the \{name.toLowerCase()}
                     */
                    public \{name} update(final UUID id, final String userName,
                                        final Locale locale, final \{name} \{name.toLowerCase()}) {
                        logger.debug("Entering update for \{name} {}", id);
                        final String query = locale == null
                                ? ""\"
                                UPDATE \{pluralName.toLowerCase()} SET title=?, description=?,
                                modified_by=? WHERE id=?
                                ""\"
                                : ""\"
                                UPDATE \{pluralName.toLowerCase()} SET modified_by=? WHERE id=?
                                ""\";
                        int updatedRows = locale == null
                                ? jdbcClient.sql(query)
                                .param(INDEX_1, \{name.toLowerCase()}.title())
                                .param(INDEX_2, \{name.toLowerCase()}.description())
                                .param(INDEX_3, userName)
                                .param(INDEX_4, id).update()
                                : jdbcClient.sql(query)
                                .param(INDEX_1, userName)
                                .param(INDEX_2, id).update();

                        if (updatedRows == 0) {
                            logger.error("Update not found {}", id);
                            throw new IllegalArgumentException("\{name} not found");
                        } else if (locale != null) {
                            updatedRows = jdbcClient.sql(""\"
                                            UPDATE \{pluralName.toLowerCase()}_localized SET title=?, locale=?,
                                            description=?
                                            WHERE \{name.toLowerCase()}_id=? AND locale=?
                                            ""\")
                                    .param(INDEX_1, \{name.toLowerCase()}.title())
                                    .param(INDEX_2, locale.getLanguage())
                                    .param(INDEX_3, \{name.toLowerCase()}.description())
                                    .param(INDEX_4, id)
                                    .param(INDEX_5, locale.getLanguage())
                                    .update();

                            if (updatedRows == 0) {

                                createLocalizedEvent(id, \{name.toLowerCase()}, locale);
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
                        return jdbcClient.sql("DELETE FROM \{pluralName.toLowerCase()} WHERE id = ?")
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
                    public List<\{name}> list(final String userName, final Locale locale) {
                        final String query = locale == null
                                ? ""\"
                                SELECT id, title, description, created_at,
                                created_by, modified_at, modified_by
                                FROM \{pluralName.toLowerCase()}
                                ""\"
                                : ""\"
                                SELECT DISTINCT e.id,
                                    CASE WHEN el.locale = ? THEN el.title
                                    ELSE e.title END AS title,
                                    CASE WHEN el.locale = ? THEN el.description
                                    ELSE e.description END AS description,
                                    e.created_at, e.created_by,
                                    e.modified_at, e.modified_by
                                FROM \{pluralName.toLowerCase()} e
                                LEFT JOIN \{pluralName.toLowerCase()}_localized el ON e.id = el.\{name.toLowerCase()}_id
                                WHERE el.locale IS NULL OR el.locale = ? OR e.id NOT IN (
                                    SELECT \{name.toLowerCase()}_id
                                    FROM \{pluralName.toLowerCase()}_localized
                                    WHERE \{name.toLowerCase()}_id = e.id AND locale = ?
                                )
                                ""\";
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
                     * Delete all the \{pluralName.toLowerCase()} related data.
                     */
                    public void delete() {
                        jdbcClient.sql("DELETE FROM \{pluralName.toLowerCase()}_localized").update();
                        jdbcClient.sql("DELETE FROM \{pluralName.toLowerCase()}").update();
                    }
                }


                                """ ;

        Files.write(
                Paths.get(RAW. "src/main/java/com/sample/service/\{name}Service.java".interpolate()),
                SERVICE_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.CREATE);
    }


    private static void generateModel(String name,String pluralName) throws IOException {
        StringTemplate MODEL_TEMPLATE = RAW
                ."""
package com.sample.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record \{name}(UUID id,
                    String title,
                    String description,
                    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                   LocalDateTime createdAt,
                    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                   String createdBy,
                    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                   LocalDateTime modifiedAt,
                    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                   String modifiedBy) {
}

                """ ;

        Files.write(
                Paths.get(RAW. "src/main/java/com/sample/model/\{name}.java".interpolate()),
                MODEL_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.CREATE);
    }


    private static void generateDDL(String name,String pluralName) throws IOException {
        StringTemplate DDL_TEMPLATE = RAW
                ."""


                CREATE TABLE \{pluralName.toLowerCase()} (
                    id UUID PRIMARY KEY,
                    title VARCHAR(55),
                    description TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_by VARCHAR(55) NOT NULL,
                    modified_at TIMESTAMP,
                    modified_by VARCHAR(200)
                );


                CREATE TABLE \{pluralName.toLowerCase()}_localized (
                    \{name.toLowerCase()}_id UUID,
                    locale VARCHAR(8) NOT NULL,
                    title VARCHAR(55),
                    description TEXT,
                    FOREIGN KEY (\{name.toLowerCase()}_id) REFERENCES \{pluralName.toLowerCase()} (id),
                    PRIMARY KEY(\{name.toLowerCase()}_id, locale)
                );
                """ ;

        Files.write(
                Paths.get("src/main/resources/db/migration/V2__app_ddl.sql"),
                DDL_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.APPEND);
    }
}
