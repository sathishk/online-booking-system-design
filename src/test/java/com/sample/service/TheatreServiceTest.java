package com.sample.service;

import com.sample.model.Theatre;
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
public class TheatreServiceTest {

    public static final String STATE_BOARD_IN_ENGLISH = "State Theatre";
    public static final String STATE_BOARD_DESCRIPTION_IN_ENGLISH = "State Theatre Description";
    public static final String STATE_BOARD_TITLE_IN_FRENCH = "Conseil d'État";
    public static final String STATE_BOARD_DESCRIPTION_IN_FRENCH = "Description du conseil d'État";
    @Autowired
    private TheatreService theatreService;

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
        theatreService.delete();
    }

    @Test
    void create() {
        final Theatre theatre = theatreService.create("mani", null,
                anEvent());
        Assertions.assertTrue(theatreService.read("mani", null, theatre.id()).isPresent(),
                "Created Theatre");
    }

    @Test
    void read() {
        final Theatre theatre = theatreService.create("mani", null,
                anEvent());
        final UUID newEventId = theatre.id();
        Assertions.assertTrue(theatreService.read("mani", null, newEventId).isPresent(),
                "Theatre Created");
    }

    @Test
    void update() {

        final Theatre theatre = theatreService.create("mani", null,
                anEvent());
        final UUID newEventId = theatre.id();
        Theatre newEvent = new Theatre(null, "Theatre", "A " +
                "Theatre",
                null, "tom", null, null);
        Theatre updatedEvent = theatreService
                .update(newEventId, "mani", null, newEvent);
        Assertions.assertEquals("Theatre", updatedEvent.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            theatreService
                    .update(UUID.randomUUID(), "mani", null, newEvent);
        });
    }

    @Test
    void delete() {

        final Theatre theatre = theatreService.create("mani", null,
                anEvent());
        theatreService.delete("mani", theatre.id());
        Assertions.assertFalse(theatreService.read("mani", null, theatre.id()).isPresent(),
                "Deleted Theatre");

    }

    @Test
    void list() {

        final Theatre theatre = theatreService.create("mani", null,
                anEvent());
        Theatre newEvent = new Theatre(null, "Theatre New", "A " +
                "Theatre",
                null, "tom", null, null);
        theatreService.create("mani", null,
                newEvent);
        List<Theatre> listoftheatre = theatreService.list("manikanta", null);
        Assertions.assertEquals(2, listoftheatre.size());

    }

    @Test
    void testLocalizationFromDefaultWithoutLocale() {
        // Create a Theatre without locale
        final Theatre theatre = theatreService.create("mani", null,
                anEvent());

        testLocalization(theatre);

    }

    @Test
    void testLocalizationFromCreateWithLocale() {
        // Create a Theatre with locale
        final Theatre theatre = theatreService.create("mani", Locale.GERMAN,
                anEvent());

        testLocalization(theatre);

    }

    void testLocalization(Theatre theatre) {

        // Update for China Language
        theatreService.update(theatre.id(), "mani", Locale.FRENCH, anEvent(theatre,
                STATE_BOARD_TITLE_IN_FRENCH,
                STATE_BOARD_DESCRIPTION_IN_FRENCH));

        // Get for french Language
        Theatre createEvent = theatreService.read("mani", Locale.FRENCH,
                theatre.id()).get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH, createEvent.description());

        final UUID id = createEvent.id();
        createEvent = theatreService.list("mani", Locale.FRENCH)
                .stream()
                .filter(theatre1 -> theatre1.id().equals(id))
                .findFirst().get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH,
                createEvent.description());

        // Get for France which does not have data
        createEvent = theatreService.read("mani", Locale.CHINESE,
                theatre.id()).get();
        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

        createEvent = theatreService.list("mani", Locale.CHINESE)
                .stream()
                .filter(theatre1 -> theatre1.id().equals(id))
                .findFirst().get();

        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

    }

    /**
     * Gets theatre.
     *
     * @return the theatre
     */
    Theatre anEvent() {
        Theatre theatre = new Theatre(null, STATE_BOARD_IN_ENGLISH,
                STATE_BOARD_DESCRIPTION_IN_ENGLISH,
                null, null,
                null, null);
        return theatre;
    }

    /**
     * Gets theatre from reference theatre.
     *
     * @return the theatre
     */
    Theatre anEvent(final Theatre ref, final String title, final String description) {
        return new Theatre(ref.id(), title,
                description,
                ref.createdAt(), ref.createdBy(),
                ref.modifiedAt(), ref.modifiedBy());
    }
}
