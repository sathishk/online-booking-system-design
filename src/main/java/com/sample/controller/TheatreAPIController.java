package com.sample.controller;

import com.sample.model.Theatre;
import com.sample.service.TheatreService;
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
@RequestMapping("/api/theatres")
@Tag(name = "Theatres", description = "Resource to manage Theatre")
class TheatreAPIController {

    /**
     * declare a theatre service.
     */
    private final TheatreService theatreService;

    TheatreAPIController(final TheatreService aTheatreService) {
        this.theatreService = aTheatreService;
    }

    /**
     * Create response entity.
     *
     * @param principal the principal
     * @param theatre     the theatre name
     * @param locale    the locale
     * @return the response entity
     */
    @Operation(summary = "Creates a new theatre",
            description = "Can be called "
                    + "only by users with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
            description = "theatre created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "theatre is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public final ResponseEntity<Theatre> create(final Principal principal,
                @RequestHeader(name = "Accept-Language",
                required = false) final Locale locale,
                          @RequestBody final Theatre theatre) {
        Theatre created = theatreService.create(principal.getName(),
                locale, theatre);
        return ResponseEntity.created(URI.create("/api/theatre"
                        + created.id()))
                .body(created);
    }


    /**
     * Read a theatre.
     *
     * @param id
     * @param principal
     * @param locale    the locale
     * @return a theatre
     */
    @Operation(summary = "Get the Theatre with given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "getting theatre successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})

    @GetMapping("/{id}")
    public final ResponseEntity<Theatre> read(@PathVariable final UUID id,
                            @RequestHeader(name = "Accept-Language",
                                          required = false) final Locale locale,
                                            final Principal principal) {
        return ResponseEntity.of(theatreService.read(principal.getName(),
                locale, id));
    }

    /**
     * Update a Theatre.
     *
     * @param id
     * @param principal
     * @param locale
     * @param theatre
     * @return a theatre
     */
    @Operation(summary = "Updates the theatre by given id",
            description = "Can be called only by users "
                    + "with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "theatre updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "theatre is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "syllabus not found")})
    @PutMapping(value = "/{id}", produces = "application/json", consumes =
            "application/json")
    public final ResponseEntity<Theatre> update(@PathVariable final UUID id,
                                              final Principal
                                                principal,
                                      @RequestHeader(name = "Accept-Language",
                                        required = false) final Locale locale,
                                              @RequestBody final Theatre
                                                theatre) {
        final Theatre updatedEvent =
                theatreService.update(id, principal.getName(),
                locale, theatre);
        return updatedEvent == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete a Theatre.
     *
     * @param id
     * @param principal
     * @return theatre
     */
    @Operation(summary = "Deletes the theatre by given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "theatre deleted successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "theatre not found")})
    @DeleteMapping("/{id}")
    public final ResponseEntity<Void> delete(@PathVariable final
                                       UUID id,
                                       final Principal principal) {
        return theatreService.delete(principal.getName(),
                id) ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * List the Theatre.
     *
     * @param principal
     * @param locale
     * @return list of theatre
     */
    @Operation(summary = "lists the theatre",
            description = " Can be invoked by auth users only",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Listing the theatre"),
            @ApiResponse(responseCode = "204",
                    description = "theatre are not available"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @GetMapping(produces = "application/json")
    public final ResponseEntity<List<Theatre>> list(final Principal
                                                    principal,
                                      @RequestHeader(name = "Accept-Language",
                                    required = false) final Locale locale) {
        final List<Theatre> theatreList = theatreService.list(
                principal.getName(), locale);
        return theatreList.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(theatreList);
    }

}
