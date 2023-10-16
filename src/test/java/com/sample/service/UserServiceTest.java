package com.sample.service;

import com.sample.model.User;
import com.sample.starter.security.payload.SignupRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    private static final String HANDLE = "tom";
    private static final String EMAIL = HANDLE + "@gmail.com";
    @Autowired
    private UserService userService;

    @BeforeEach
    void before()  {
        cleanup();
    }

    @AfterEach
    void after()  {
        cleanup();
    }

    private void cleanup()  {
        userService.delete();
    }

    @Test
    void testSignUp()  {
        userService.signUp(aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));

        Assertions.assertTrue(userService.read(HANDLE).isPresent());
        Assertions.assertTrue(userService.readByEmail(EMAIL).isPresent());
    }

    @Test
    void testEmptyReads()  {
        Assertions.assertFalse(userService.read(HANDLE).isPresent());
        Assertions.assertFalse(userService.readByEmail(EMAIL).isPresent());
    }

    @Test
    void testInvalidSignUp() {
        SignupRequest signupRequest = aSignupRequest();

        signupRequest.setEmail("Invalid Email");

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            userService.signUp(signupRequest,
                    s -> String.valueOf(new StringBuilder(s).reverse()));
        });

    }

    @Test
    void testUpdate()  {
        userService.signUp(aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));

        User user = getUserWithNewPassword(
                userService.readByEmail(EMAIL).get());

        userService.update(HANDLE, user);

        Assertions.assertEquals(user.password(),
                userService.readByEmail(EMAIL).get().password());


    }

    @Test
    void testInvalidUpdate()  {
        userService.signUp(aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));

        User user = getUserWithNewPassword(
                userService.readByEmail(EMAIL).get());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.update(HANDLE + "INVALID", user);
        });
    }


    private static User getUserWithNewPassword(final User existingUser) {
        return new User(
                existingUser.userHandle(),
                existingUser.email(),
                String.valueOf(System.currentTimeMillis()),
                existingUser.imageUrl(),
                existingUser.provider(),
                existingUser.createdAt(),
                existingUser.modifiedAt());
    }

    SignupRequest aSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(EMAIL);
        signupRequest.setImageUrl("/images/" + HANDLE + ".png");
        signupRequest.setPassword("password");
        return signupRequest;
    }
}