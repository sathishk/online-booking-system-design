package com.sample.service;

import com.sample.model.Tag;
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
public class TagServiceTest {

    public static final String STATE_BOARD_IN_ENGLISH = "State Tag";
    public static final String STATE_BOARD_DESCRIPTION_IN_ENGLISH = "State Tag Description";
    public static final String STATE_BOARD_TITLE_IN_FRENCH = "Conseil d'État";
    public static final String STATE_BOARD_DESCRIPTION_IN_FRENCH = "Description du conseil d'État";
    @Autowired
    private TagService tagService;

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
        tagService.delete();
    }

    @Test
    void create() {
        final Tag tag = tagService.create("mani", null,
                anEvent());
        Assertions.assertTrue(tagService.read("mani", null, tag.id()).isPresent(),
                "Created Tag");
    }

    @Test
    void read() {
        final Tag tag = tagService.create("mani", null,
                anEvent());
        final UUID newEventId = tag.id();
        Assertions.assertTrue(tagService.read("mani", null, newEventId).isPresent(),
                "Tag Created");
    }

    @Test
    void update() {

        final Tag tag = tagService.create("mani", null,
                anEvent());
        final UUID newEventId = tag.id();
        Tag newEvent = new Tag(null, "Tag", "A " +
                "Tag",
                null, "tom", null, null);
        Tag updatedEvent = tagService
                .update(newEventId, "mani", null, newEvent);
        Assertions.assertEquals("Tag", updatedEvent.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tagService
                    .update(UUID.randomUUID(), "mani", null, newEvent);
        });
    }

    @Test
    void delete() {

        final Tag tag = tagService.create("mani", null,
                anEvent());
        tagService.delete("mani", tag.id());
        Assertions.assertFalse(tagService.read("mani", null, tag.id()).isPresent(),
                "Deleted Tag");

    }

    @Test
    void list() {

        final Tag tag = tagService.create("mani", null,
                anEvent());
        Tag newEvent = new Tag(null, "Tag New", "A " +
                "Tag",
                null, "tom", null, null);
        tagService.create("mani", null,
                newEvent);
        List<Tag> listoftag = tagService.list("manikanta", null);
        Assertions.assertEquals(2, listoftag.size());

    }

    @Test
    void testLocalizationFromDefaultWithoutLocale() {
        // Create a Tag without locale
        final Tag tag = tagService.create("mani", null,
                anEvent());

        testLocalization(tag);

    }

    @Test
    void testLocalizationFromCreateWithLocale() {
        // Create a Tag with locale
        final Tag tag = tagService.create("mani", Locale.GERMAN,
                anEvent());

        testLocalization(tag);

    }

    void testLocalization(Tag tag) {

        // Update for China Language
        tagService.update(tag.id(), "mani", Locale.FRENCH, anEvent(tag,
                STATE_BOARD_TITLE_IN_FRENCH,
                STATE_BOARD_DESCRIPTION_IN_FRENCH));

        // Get for french Language
        Tag createEvent = tagService.read("mani", Locale.FRENCH,
                tag.id()).get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH, createEvent.description());

        final UUID id = createEvent.id();
        createEvent = tagService.list("mani", Locale.FRENCH)
                .stream()
                .filter(tag1 -> tag1.id().equals(id))
                .findFirst().get();
        Assertions.assertEquals(STATE_BOARD_TITLE_IN_FRENCH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_FRENCH,
                createEvent.description());

        // Get for France which does not have data
        createEvent = tagService.read("mani", Locale.CHINESE,
                tag.id()).get();
        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

        createEvent = tagService.list("mani", Locale.CHINESE)
                .stream()
                .filter(tag1 -> tag1.id().equals(id))
                .findFirst().get();

        Assertions.assertEquals(STATE_BOARD_IN_ENGLISH, createEvent.title());
        Assertions.assertEquals(STATE_BOARD_DESCRIPTION_IN_ENGLISH, createEvent.description());

    }

    /**
     * Gets tag.
     *
     * @return the tag
     */
    Tag anEvent() {
        Tag tag = new Tag(null, STATE_BOARD_IN_ENGLISH,
                STATE_BOARD_DESCRIPTION_IN_ENGLISH,
                null, null,
                null, null);
        return tag;
    }

    /**
     * Gets tag from reference tag.
     *
     * @return the tag
     */
    Tag anEvent(final Tag ref, final String title, final String description) {
        return new Tag(ref.id(), title,
                description,
                ref.createdAt(), ref.createdBy(),
                ref.modifiedAt(), ref.modifiedBy());
    }
}
