package com.sample.service;

import com.sample.model.Genre;
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
public class GenreServiceTest {

    public static final String STATE_BOARD_IN_ENGLISH = "State Genre";
    public static final String STATE_BOARD_DESCRIPTION_IN_ENGLISH = "State Genre Description";
    public static final String STATE_BOARD_TITLE_IN_FRENCH = "Conseil d'État";
    public static final String STATE_BOARD_DESCRIPTION_IN_FRENCH = "Description du conseil d'État";
    @Autowired
    private GenreService genreService;

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
        genreService.delete();
    }

    @Test
    void create() {
        final Genre genre = genreService.create("mani", null,
                anEvent());
        Assertions.assertTrue(genreService.read("mani", null, genre.id()).isPresent(),
                "Created Genre");
    }

    @Test
    void read() {
        final Genre genre = genreService.create("mani", null,
                anEvent());
        final UUID newEventId = genre.id();
        Assertions.assertTrue(genreService.read("mani", null, newEventId).isPresent(),
                "Genre Created");
    }

    @Test
    void update() {

        final Genre genre = genreService.create("mani", null,
                anEvent());
        final UUID newEventId = genre.id();
        Genre newEvent = new Genre(null, "Genre", "A " +
                "Genre",
                null, "tom", null, null);
        Genre updatedEvent = genreService
                .update(newEventId, "mani", null, newEvent);
        Assertions.assertEquals("Genre", updatedEvent.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            genreService
                    .update(UUID.randomUUID(), "mani", null, newEvent);
        });
    }

    @Test
    void delete() {

        final Genre genre = genreService.create("mani", null,
                anEvent());
        genreService.delete("mani", genre.id());
        Assertions.assertFalse(genreService.read("mani", null, genre.id()).isPresent(),
                "Deleted Genre");

    }

    @Test
    void list() {

        final Genre genre = genreService.create("mani", null,
                anEvent());
        Genre newEvent = new Genre(null, "Genre New", "A " +
                "Genre",
                null, "tom", null, null);
        genreService.create("mani", null,
                newEvent);
        List<Genre> listofgenre = genreService.list("manikanta", null);
        Assertions.assertEquals(2, listofgenre.size());

    }

    @Test
    void testLocalizationFromDefaultWithoutLocale() {
        // Create a Genre without locale
        final Genre genre = genreService.create("mani", null,
                anEvent());

        testLocalization(genre);

    }

    @Test
    void testLocalizationFromCreateWithLocale() {
        // Create a Genre with locale
        final Genre genre = genreService.create("mani", Locale.GERMAN,
                anEvent());

        testLocalization(genre);

    }

    void testLocalization(Genre genre) {

        // Update for China Language
        genreService.update(genre.id(), "mani", Locale.FRENCH, anEvent(genre,
                STATE_BOARD_TITLE_IN_FRENCH,
                STATE_BOARD_DESCRIPTION_IN_FRENCH));

        // Get for french Language
        Genre createEvent = genreService.read("mani", Locale.FRENCH,
                genre.id()).get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH, createEvent.description());

        final UUID id = createEvent.id();
        createEvent = genreService.list("mani", Locale.FRENCH)
                .stream()
                .filter(genre1 -> genre1.id().equals(id))
                .findFirst().get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH,
                createEvent.description());

        // Get for France which does not have data
        createEvent = genreService.read("mani", Locale.CHINESE,
                genre.id()).get();
        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

        createEvent = genreService.list("mani", Locale.CHINESE)
                .stream()
                .filter(genre1 -> genre1.id().equals(id))
                .findFirst().get();

        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

    }

    /**
     * Gets genre.
     *
     * @return the genre
     */
    Genre anEvent() {
        Genre genre = new Genre(null, STATE_BOARD_IN_ENGLISH,
                STATE_BOARD_DESCRIPTION_IN_ENGLISH,
                null, null,
                null, null);
        return genre;
    }

    /**
     * Gets genre from reference genre.
     *
     * @return the genre
     */
    Genre anEvent(final Genre ref, final String title, final String description) {
        return new Genre(ref.id(), title,
                description,
                ref.createdAt(), ref.createdBy(),
                ref.modifiedAt(), ref.modifiedBy());
    }
}
