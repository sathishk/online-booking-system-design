package com.sample.controller;

import com.sample.model.Genre;
import com.sample.service.GenreService;
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
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "Resource to manage Genre")
class GenreAPIController {

    /**
     * declare a genre service.
     */
    private final GenreService genreService;

    GenreAPIController(final GenreService aGenreService) {
        this.genreService = aGenreService;
    }

    /**
     * Create response entity.
     *
     * @param principal the principal
     * @param genre     the genre name
     * @param locale    the locale
     * @return the response entity
     */
    @Operation(summary = "Creates a new genre",
            description = "Can be called "
                    + "only by users with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
            description = "genre created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "genre is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public final ResponseEntity<Genre> create(final Principal principal,
                @RequestHeader(name = "Accept-Language",
                required = false) final Locale locale,
                          @RequestBody final Genre genre) {
        Genre created = genreService.create(principal.getName(),
                locale, genre);
        return ResponseEntity.created(URI.create("/api/genre"
                        + created.id()))
                .body(created);
    }


    /**
     * Read a genre.
     *
     * @param id
     * @param principal
     * @param locale    the locale
     * @return a genre
     */
    @Operation(summary = "Get the Genre with given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "getting genre successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})

    @GetMapping("/{id}")
    public final ResponseEntity<Genre> read(@PathVariable final UUID id,
                            @RequestHeader(name = "Accept-Language",
                                          required = false) final Locale locale,
                                            final Principal principal) {
        return ResponseEntity.of(genreService.read(principal.getName(),
                locale, id));
    }

    /**
     * Update a Genre.
     *
     * @param id
     * @param principal
     * @param locale
     * @param genre
     * @return a genre
     */
    @Operation(summary = "Updates the genre by given id",
            description = "Can be called only by users "
                    + "with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "genre updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "genre is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})
    @PutMapping(value = "/{id}", produces = "application/json", consumes =
            "application/json")
    public final ResponseEntity<Genre> update(@PathVariable final UUID id,
                                              final Principal
                                                principal,
                                      @RequestHeader(name = "Accept-Language",
                                        required = false) final Locale locale,
                                              @RequestBody final Genre
                                                genre) {
        final Genre updatedEvent =
                genreService.update(id, principal.getName(),
                locale, genre);
        return updatedEvent == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete a Genre.
     *
     * @param id
     * @param principal
     * @return genre
     */
    @Operation(summary = "Deletes the genre by given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "genre deleted successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "genre not found")})
    @DeleteMapping("/{id}")
    public final ResponseEntity<Void> delete(@PathVariable final
                                       UUID id,
                                       final Principal principal) {
        return genreService.delete(principal.getName(),
                id) ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * List the Genre.
     *
     * @param principal
     * @param locale
     * @return list of genre
     */
    @Operation(summary = "lists the genre",
            description = " Can be invoked by auth users only",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Listing the genre"),
            @ApiResponse(responseCode = "204",
                    description = "genre are not available"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @GetMapping(produces = "application/json")
    public final ResponseEntity<List<Genre>> list(final Principal
                                                    principal,
                                      @RequestHeader(name = "Accept-Language",
                                    required = false) final Locale locale) {
        final List<Genre> genreList = genreService.list(
                principal.getName(), locale);
        return genreList.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(genreList);
    }

}
