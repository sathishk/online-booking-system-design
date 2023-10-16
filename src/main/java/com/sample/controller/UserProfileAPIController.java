package com.sample.controller;

import com.sample.model.UserProfile;
import com.sample.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

/**
 * The type User profile api controller.
 */
@RestController
@RequestMapping("/api/profile")
@Tag(name = "User Profiles",
        description = "Resources to manage User Profile")
class UserProfileAPIController {

    /**
     * declare a userprofile service.
     */
    private final UserProfileService userProfileService;

    /**
     * Instantiates a new User profile api controller.
     *
     * @param aUserProfileService the user profile service
     */
    UserProfileAPIController(final UserProfileService
                                        aUserProfileService) {
        this.userProfileService = aUserProfileService;
    }

    /**
     * Create response entity.
     *
     * @param principal      the principal
     * @param userProfile the user profile
     * @return the response entity
     */
    @Operation(summary = "creates a new User Profile",
            description = "Can be called "
                    + "only by users with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
            description = "user profile created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "user profile is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public final ResponseEntity<UserProfile> create(
                                        final Principal principal,
                                                 final @RequestBody
                                                 UserProfile
                                                         userProfile
                                                 ) {
        UserProfile created = userProfileService
                .create(userProfile);
        return ResponseEntity.created(
                URI.create("/api/profile" + created.userHandle()))
                .body(created);
    }

    /**
     * Read response entity.
     *
     * @param principal the principal
     * @param id        the id
     * @return the response entity
     */
    @Operation(summary = "Get the UserProfile with given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "getting UserProfile successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "UserProfile not found")})
    @GetMapping(value = "/{id}", produces = "application/json")
    public final ResponseEntity<UserProfile> read(final Principal principal,
                                      final @PathVariable String id) {
        return ResponseEntity.of(userProfileService.read(
                id));
    }

}
