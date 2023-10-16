package com.sample.starter.security.oauth2.service;


import com.sample.model.User;
import com.sample.starter.security.config.UserPrincipal;
import com.sample.starter.security.exception.OAuth2AuthenticationProcessingException;
import com.sample.starter.security.oauth2.user.OAuth2UserInfo;
import com.sample.starter.security.oauth2.user.OAuth2UserInfoFactory;
import com.sample.starter.security.payload.SignupRequest;
import com.sample.model.AuthProvider;
import com.sample.service.UserProfileService;
import com.sample.service.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.Optional;

/**
 * The type Custom o auth 2 user service.
 */
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * User Details Service.
     */
    private final UserService userService;

    /**
     * User Details Service.
     */
    private final UserProfileService userProfileService;

    /**
     * CustomOAuth2UserService.
     * @param auserProfileService
     * @param anUserRepository user repository
     */
    public CustomOAuth2UserService(final UserService
                                           anUserRepository,
                                   final UserProfileService
                                           auserProfileService) {
        this.userService = anUserRepository;
        this.userProfileService = auserProfileService;
    }

    /**
     * Loads the user.
     *
     * @param oAuth2UserRequest request
     * @return OAuth2User
     * @throws OAuth2AuthenticationException auth to authentication exception
     */
    @Override
    public OAuth2User loadUser(final OAuth2UserRequest oAuth2UserRequest)
            throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (final AuthenticationException ex) {
            throw ex;
        } catch (final Exception ex) {
            // Throwing an instance of AuthenticationException will
            // trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(),
                    ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(final
                         OAuth2UserRequest oAuth2UserRequest,
                         final OAuth2User oAuth2User) throws SQLException {
        final OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration()
                        .getRegistrationId(), oAuth2User.getAttributes());
        if (!StringUtils.hasLength(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException(
                    "Email not found from OAuth2 provider");
        }

        final Optional<User> userOptional =
                userService.readByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.provider().equals(getAuthProvider(oAuth2UserRequest))) {
                throw new OAuth2AuthenticationProcessingException(
                        "Looks like you're signed up with "
                                + user.provider()
                                + " account. Please use your "
                                + user.provider()
                                + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user,
                userProfileService.read(user.userHandle()),
                oAuth2User.getAttributes());
    }

    private User registerNewUser(final OAuth2UserRequest oAuth2UserRequest,
                                 final OAuth2UserInfo oAuth2UserInfo)
            throws SQLException {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(oAuth2UserInfo.getEmail());
        signupRequest.setPassword(oAuth2UserInfo.getEmail());
        signupRequest.setImageUrl(oAuth2UserInfo.getImageUrl());
        signupRequest.setAuthProvider(getAuthProvider(oAuth2UserRequest));
        userService.signUp(signupRequest, s -> s);
        return userService.readByEmail(oAuth2UserInfo.getEmail()).get();
    }

    private static AuthProvider getAuthProvider(
            final OAuth2UserRequest oAuth2UserRequest) {
        return AuthProvider.valueOf(
                oAuth2UserRequest.getClientRegistration()
                        .getRegistrationId());
    }

    private User updateExistingUser(final User existingUser,
                                    final OAuth2UserInfo oAuth2UserInfo)
                                throws SQLException {
        return userService.update(existingUser.userHandle(),
                new User(null, oAuth2UserInfo.getEmail(),
                        null,
                        oAuth2UserInfo.getImageUrl(),
                        existingUser.provider(),
                        null, null));
    }

}
