package com.sample.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.model.User;
import com.sample.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.security.Principal;
import java.sql.SQLException;

/**
 * The type User api controller.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "Resources to manage User")
class UserAPIController {

    /**
     * declare a user service.
     */
    private final UserService userService;

    /**
     * @param auserService a user service
     */
    UserAPIController(final UserService auserService) {
        this.userService = auserService;
    }


    @Operation(summary = "Get the User with given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "getting user successfully"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "user not found")})
    @GetMapping(value = "/{id}", produces = "application/json")
    public final ResponseEntity<User> read(final Principal principal,
                                           final @PathVariable String id)
            throws SQLException {
        return ResponseEntity.of(userService.read(
                id));
    }

    @Operation(summary = "Updates the user by given id",
            description = "Can be called only by users "
                    + "with 'auth management' rights.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "user updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "user is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "invalid credentials"),
            @ApiResponse(responseCode = "404",
                    description = "user not found")})
    @PutMapping(value = "/{id}", produces = "application/json", consumes =
            "application/json")
    public final ResponseEntity<User> update(final @PathVariable String id,
                                             final Principal principal,
                                             final @RequestBody User user)
            throws JsonProcessingException, SQLException {
        final User updatedUser = userService.update(id,
                user);
        return ResponseEntity.ok(updatedUser);
    }


}
