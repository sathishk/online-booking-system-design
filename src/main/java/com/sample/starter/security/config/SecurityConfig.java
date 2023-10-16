package com.sample.starter.security.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.starter.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sample.starter.security.filter.TokenAuthenticationFilter;
import com.sample.starter.security.oauth2.service.CustomOAuth2UserService;
import com.sample.starter.security.oauth2.service.OAuth2AuthenticationFailureHandler;
import com.sample.starter.security.oauth2.service.OAuth2AuthenticationSuccessHandler;
import com.sample.service.UserProfileService;
import com.sample.service.UserService;
import com.sample.starter.security.service.AuthenticationService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * The type Security config.
 */
@Configuration
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@EnableConfigurationProperties(AppProperties.class)
public class SecurityConfig {

    /**
     * User Details Service.
     */
    private final UserService userService;

    /**
     * User Details Service.
     */
    private final UserProfileService userProfileService;

    /**
     * inject the customUserDetailsService object dependency.
     */
    private final UserDetailsService userDetailsService;


    /**
     * TokenAuthenticationFilter.
     */
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * TokenProvider.
     */
    private final AuthenticationService authenticationService;

    /**
     * inject the customOAuth2UserService object dependency.
     */
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * inject the oAuth2AuthenticationSuccessHandler object dependency.
     */
    private final OAuth2AuthenticationSuccessHandler
            oAuth2AuthenticationSuccessHandler;

    /**
     * By default, Spring OAuth2 uses
     * HttpSessionOAuth2AuthorizationRequestRepository to save.
     * <p>
     * the authorization request. But, since our service is stateless,
     * we can't save it in
     * the session. We'll save the request in a
     * Base64 encoded cookie instead.
     */
    private final HttpCookieOAuth2AuthorizationRequestRepository
            cookieAuthRepo;

    /**
     * inject the oAuth2AuthenticationFailureHandler object dependency.
     */
    private final OAuth2AuthenticationFailureHandler
            oAuth2AuthenticationFailureHandler;

    /**
     * Creates Security Config.
     *
     * @param auserService
     * @param auserProfileService
     * @param appProperties          properties
     * @param aCacheManager          aCacheManager
     * @param objectMapper
     * @param auserDetailsService
     */
    public SecurityConfig(final UserService auserService,
                          final UserProfileService auserProfileService,
                          final AppProperties appProperties,
                          final CacheManager aCacheManager,
                          final ObjectMapper objectMapper,
                          final UserDetailsService auserDetailsService) {
        this.userService = auserService;
        this.userProfileService = auserProfileService;

        userDetailsService = auserDetailsService;
        authenticationService = new AuthenticationService(appProperties,
                aCacheManager, objectMapper, userDetailsService,
                userProfileService);


        tokenAuthenticationFilter = new TokenAuthenticationFilter(
                authenticationService);


        cookieAuthRepo = new
                HttpCookieOAuth2AuthorizationRequestRepository();
        oAuth2AuthenticationSuccessHandler = new
                OAuth2AuthenticationSuccessHandler(authenticationService,
                appProperties,
                cookieAuthRepo);
        oAuth2AuthenticationFailureHandler = new
                OAuth2AuthenticationFailureHandler(
                cookieAuthRepo);


        customOAuth2UserService = new CustomOAuth2UserService(
                this.userService,
                this.userProfileService);
    }

    /**
     * Aithe Provide.
     *
     * @return authenticationProvider
     */
    @Bean
    public AuthenticationService tokenProvider() {
        return authenticationService;
    }

    /**
     * Hi.
     *
     * @return webSecurityCustomizer
     * @throws Exception exception
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(antMatcher("/api/metrics/**"),
                        antMatcher("/h2-console"),
                        antMatcher("/h2-console/**"),
                        antMatcher("/swagger-ui.html"),
                        antMatcher("/swagger-ui/*"),
                        antMatcher("/v3/api-docs"),
                        antMatcher("/v3/api-docs/*"),
                        antMatcher("/api/auth/login"),
                        antMatcher("/welcome")
                );
    }

    /**
     * method configure is overrided here.
     *
     * @param http http
     * @return filterChain
     * @throws Exception exception
     */
    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(antMatcher("/api/auth/login"),
                                antMatcher("/api/auth/signup"),
                                antMatcher("/api/auth/welcome"),
                                antMatcher("/favicon.ico"),
                                antMatcher("/error"),
                                antMatcher("/h2-console"),
                                antMatcher("/h2-console/**"),
                                antMatcher("/movies"),
                                antMatcher("/movies/**"),
                                antMatcher("/ta/movies"),
                                antMatcher("/ta/movies/**"),
                                antMatcher("/v3/api-docs"),
                                antMatcher("/welcome"),
                                antMatcher("/oauth2/**"),
                                antMatcher("/oauth2/**/**")
                        )
                        .permitAll()
                        .requestMatchers(toH2Console()).permitAll()
                        .anyRequest()
                        .authenticated())
                .cors(config -> {
                })
                .sessionManagement(config -> config
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(
                                new RestAuthenticationEntryPoint()))
                .headers(config -> config
                        .frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable));
        http.oauth2Login(httpSecurityOAuth2LoginConfigurer
                -> httpSecurityOAuth2LoginConfigurer
                .authorizationEndpoint(
                        authorizationEndpointConfig
                                -> authorizationEndpointConfig
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(
                                        cookieAuthRepo))
                .redirectionEndpoint(
                        redirectionEndpointConfig
                                -> redirectionEndpointConfig
                                .baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(userInfoEndpointConfig
                        -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(
                        oAuth2AuthenticationSuccessHandler)
                .failureHandler(
                        oAuth2AuthenticationFailureHandler));

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * In Production we can controld the filters from application.yml.
     *
     * @return corsFilter cors filter
     */
    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.setAllowedMethods(
                List.of("GET",
                        "POST", "PUT", "PATCH",
                        "DELETE", "OPTIONS"));

        final UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
