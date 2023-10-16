package com.sample.starter.security.controller;

import com.sample.starter.security.payload.AuthenticationRequest;
import com.sample.starter.security.payload.AuthenticationResponse;
import com.sample.starter.security.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * The type Authentication api controller.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login",
        description = "Resource to manage Login and Signup")
class LoginController {

    /**
     * LoginService instance.
     */
    private final LoginService loginService;

    /**
     * Instance of LoginController.
     *
     * @param aLoginService
     */
    LoginController(final LoginService aLoginService) {
        this.loginService = aLoginService;
    }

    /**
     * performs the login function.
     *
     * @param authenticationRequest the authentication request
     * @return authentication response
     */
    @Operation(summary = "Login with credentials")
    @PostMapping("/login")
    final ResponseEntity<AuthenticationResponse> login(
            final @RequestBody
            AuthenticationRequest
                    authenticationRequest) throws SQLException {

        return ResponseEntity.ok().body(
                loginService.login(authenticationRequest));
    }
}
