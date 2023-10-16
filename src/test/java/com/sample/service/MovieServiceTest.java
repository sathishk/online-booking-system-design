package com.sample.service;

import com.sample.model.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest
public class MovieServiceTest {

    public static final String STATE_BOARD_IN_ENGLISH = "State Movie";
    public static final String STATE_BOARD_DESCRIPTION_IN_ENGLISH = "State Movie Description";
    public static final String STATE_BOARD_TITLE_IN_FRENCH = "Conseil d'État";
    public static final String STATE_BOARD_DESCRIPTION_IN_FRENCH = "Description du conseil d'État";
    @Autowired
    private MovieService movieService;

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
        movieService.delete();
    }

    @Test
    void create() {
        final Movie event = movieService.create("mani", null,
                anEvent());
        Assertions.assertTrue(movieService.read("mani", null, event.id()).isPresent(),
                "Created Movie");
    }

    @Test
    void read() {
        final Movie event = movieService.create("mani", null,
                anEvent());
        final UUID newEventId = event.id();
        Assertions.assertTrue(movieService.read("mani", null, newEventId).isPresent(),
                "Movie Created");
    }

    @Test
    void update() {

        final Movie event = movieService.create("mani", null,
                anEvent());
        final UUID newEventId = event.id();
        Movie newEvent = new Movie(null, "Movie", "A " +
                "Movie", LocalDate.now(),
                null, "tom", null, null);
        Movie updatedEvent = movieService
                .update(newEventId, "mani", null, newEvent);
        Assertions.assertEquals("Movie", updatedEvent.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            movieService
                    .update(UUID.randomUUID(), "mani", null, newEvent);
        });
    }

    @Test
    void delete() {

        final Movie event = movieService.create("mani", null,
                anEvent());
        movieService.delete("mani", event.id());
        Assertions.assertFalse(movieService.read("mani", null, event.id()).isPresent(),
                "Deleted Movie");

    }

    @Test
    void list() {

        final Movie event = movieService.create("mani", null,
                anEvent());
        Movie newEvent = new Movie(null, "Movie New", "A " +
                "Movie", LocalDate.now(),
                null, "tom", null, null);
        movieService.create("mani", null,
                newEvent);
        List<Movie> listofevent = movieService.list("manikanta", null);
        Assertions.assertEquals(2, listofevent.size());

    }

    @Test
    void testLocalizationFromDefaultWithoutLocale() {
        // Create a Movie without locale
        final Movie event = movieService.create("mani", null,
                anEvent());

        testLocalization(event);

    }

    @Test
    void testLocalizationFromCreateWithLocale() {
        // Create a Movie with locale
        final Movie event = movieService.create("mani", Locale.GERMAN,
                anEvent());

        testLocalization(event);

    }

    void testLocalization(Movie event) {

        // Update for China Language
        movieService.update(event.id(), "mani", Locale.FRENCH, anEvent(event,
                STATE_BOARD_TITLE_IN_FRENCH,
                STATE_BOARD_DESCRIPTION_IN_FRENCH));

        // Get for french Language
        Movie createEvent = movieService.read("mani", Locale.FRENCH,
                event.id()).get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH, createEvent.description());

        final UUID id = createEvent.id();
        createEvent = movieService.list("mani", Locale.FRENCH)
                .stream()
                .filter(event1 -> event1.id().equals(id))
                .findFirst().get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH,
                createEvent.description());

        // Get for France which does not have data
        createEvent = movieService.read("mani", Locale.CHINESE,
                event.id()).get();
        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

        createEvent = movieService.list("mani", Locale.CHINESE)
                .stream()
                .filter(event1 -> event1.id().equals(id))
                .findFirst().get();

        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

    }

    /**
     * Gets event.
     *
     * @return the event
     */
    Movie anEvent() {
        Movie event = new Movie(null, STATE_BOARD_IN_ENGLISH,
                STATE_BOARD_DESCRIPTION_IN_ENGLISH, LocalDate.now().plusDays(1L),
                null, null,
                null, null);
        return event;
    }

    /**
     * Gets event from reference event.
     *
     * @return the event
     */
    Movie anEvent(final Movie ref, final String title, final String description) {
        return new Movie(ref.id(), title,
                description, ref.releaseDate(),
                ref.createdAt(), ref.createdBy(),
                ref.modifiedAt(), ref.modifiedBy());
    }
}