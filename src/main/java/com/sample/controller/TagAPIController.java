package com.sample.controller;

import com.sample.model.Tag;
import com.sample.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags",
        description = "Resource to manage Tag")
class TagAPIController {

    /**
     * declare a tag service.
     */
    private final TagService tagService;

    TagAPIController(final TagService aTagService) {
        this.tagService = aTagService;
    }

    /**
     * Create response entity.
     *
     * @param principal the principal
     * @param tag     the tag name
     * @param locale    the locale
     * @return the response entity
     */
    @Operation(summary = "Creates a new tag",
            description = "Can be called "
                    + "only by users with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
            description = "tag created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "tag is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public final ResponseEntity<Tag> create(final Principal principal,
                @RequestHeader(name = "Accept-Language",
                required = false) final Locale locale,
                          @RequestBody final Tag tag) {
        Tag created = tagService.create(principal.getName(),
                locale, tag);
        return ResponseEntity.created(URI.create("/api/tag"
                        + created.id()))
                .body(created);
    }


    /**
     * Read a tag.
     *
     * @param id
     * @param principal
     * @param locale    the locale
     * @return a tag
     */
    @Operation(summary = "Get the Tag with given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "getting tag successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})

    @GetMapping("/{id}")
    public final ResponseEntity<Tag> read(@PathVariable final UUID id,
                            @RequestHeader(name = "Accept-Language",
                                          required = false) final Locale locale,
                                            final Principal principal) {
        return ResponseEntity.of(tagService.read(principal.getName(),
                locale, id));
    }

    /**
     * Update a Tag.
     *
     * @param id
     * @param principal
     * @param locale
     * @param tag
     * @return a tag
     */
    @Operation(summary = "Updates the tag by given id",
            description = "Can be called only by users "
                    + "with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "tag updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "tag is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})
    @PutMapping(value = "/{id}", produces = "application/json", consumes =
            "application/json")
    public final ResponseEntity<Tag> update(@PathVariable final UUID id,
                                              final Principal
                                                principal,
                                      @RequestHeader(name = "Accept-Language",
                                        required = false) final Locale locale,
                                              @RequestBody final Tag
                                                tag) {
        final Tag updatedEvent =
                tagService.update(id, principal.getName(),
                locale, tag);
        return updatedEvent == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete a Tag.
     *
     * @param id
     * @param principal
     * @return tag
     */
    @Operation(summary = "Deletes the tag by given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "tag deleted successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "tag not found")})
    @DeleteMapping("/{id}")
    public final ResponseEntity<Void> delete(@PathVariable final
                                       UUID id,
                                       final Principal principal) {
        return tagService.delete(principal.getName(),
                id) ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * List the Tag.
     *
     * @param principal
     * @param locale
     * @return list of tag
     */
    @Operation(summary = "lists the tag",
            description = " Can be invoked by auth users only",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Listing the tag"),
            @ApiResponse(responseCode = "204",
                    description = "tag are not available"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @GetMapping(produces = "application/json")
    public final ResponseEntity<List<Tag>> list(final Principal
                                                    principal,
                                      @RequestHeader(name = "Accept-Language",
                                    required = false) final Locale locale) {
        final List<Tag> tagList = tagService.list(
                principal.getName(), locale);
        return tagList.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tagList);
    }

}
