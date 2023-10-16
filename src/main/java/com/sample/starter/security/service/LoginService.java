package com.sample.starter.security.service;

import com.sample.starter.security.payload.AuthenticationRequest;
import com.sample.starter.security.payload.AuthenticationResponse;
import com.sample.starter.security.payload.SignupRequest;
import com.sample.model.AuthProvider;
import com.sample.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * The type Login service.
 */
@Service
public class LoginService {

    /**
     * UserService instance.
     */
    private final UserService userService;

    /**
     * PasswordEncoder instance.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * AuthenticationManager instance.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * TokenProvider instance.
     */
    private final AuthenticationService authenticationService;

    /**
     * Instantiates a new Login service.
     *
     * @param aUserService        the a user service
     * @param aPasswordEncoder       the a password encoder
     * @param aAuthenticationManager the a authentication manager
     * @param aAuthenticationService         the a token provider
     */
    public LoginService(final UserService aUserService,
                        final PasswordEncoder aPasswordEncoder,
                        final AuthenticationManager aAuthenticationManager,
                        final AuthenticationService aAuthenticationService) {
        this.userService = aUserService;
        this.passwordEncoder = aPasswordEncoder;
        this.authenticationManager = aAuthenticationManager;
        this.authenticationService = aAuthenticationService;
    }

    /**
     * Sign up.
     *
     * @param authenticationRequest authenticationRequest
     */
    private void signUp(final AuthenticationRequest
                                authenticationRequest) throws SQLException {
        // Then Sign Up
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(authenticationRequest.getUserName());
        signupRequest.setPassword(authenticationRequest.getPassword());
        signupRequest.setAuthProvider(AuthProvider.local);
        signupRequest.setImageUrl("/images/"
                + authenticationRequest.getUserName().split("@")[0]
                + ".png");
        userService.signUp(signupRequest,
                passwordEncoder::encode);
    }

    /**
     * Login authentication response.
     *
     * @param authenticationRequest the authentication request
     * @return the authentication response
     */
    public AuthenticationResponse login(final AuthenticationRequest
                                    authenticationRequest) throws SQLException {
        try {
            return authenticationService.getAuthenticationResponse(
                    this.authenticationManager
                    .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authenticationRequest.getUserName(),
                                authenticationRequest.getPassword())));
        } catch (final BadCredentialsException credentialsException) {
            // If New User
            if (userService.readByEmail(
                    authenticationRequest.getUserName()).isEmpty()) {
                // Then Sign Up
                signUp(authenticationRequest);
                return login(authenticationRequest);
            }
            throw new BadCredentialsException("Invalid Login Credentials");
        }
    }
}
