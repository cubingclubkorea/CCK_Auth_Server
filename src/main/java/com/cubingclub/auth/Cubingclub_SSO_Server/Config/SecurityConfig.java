package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service.OAuthClientService;
import com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Service.RsaKeysService;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
    @Lazy
    private final OAuthClientService oAuthClientService;
    private final RsaKeysService rsaKeysService;
    private final CorsProperties corsProperties;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtBlacklistFilter jwtBlacklistFilter;
    private final OAuth2AuthorizationSuccessHandler authorizationSuccessHandler;
    private final OAuth2TokenResponseHandler tokenResponseHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())
                                .authorizationEndpoint(authorizationEndpoint ->
                                        authorizationEndpoint
                                                .consentPage("/consent")
                                                .authorizationResponseHandler(authorizationSuccessHandler)
                                )
                                .tokenEndpoint(tokenEndpoint ->
                                        tokenEndpoint.accessTokenResponseHandler(tokenResponseHandler)
                                )
                );
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint("/login"),
                                new org.springframework.security.web.util.matcher.MediaTypeRequestMatcher(org.springframework.http.MediaType.TEXT_HTML)
                        )
                )
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form->form
                        .loginPage("/login")
                        .permitAll())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(Customizer.withDefaults())
                )
                .addFilterBefore(jwtBlacklistFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form->form
                        .loginPage("/login")
                        .permitAll())
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/oauth2/token/revoke").permitAll()
                        .requestMatchers("/api/auth/info", "/api/auth/edit", "/api/oauth-client/**", "/api/rfid/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAnyRole("ROOT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admin/**").hasAnyRole("ROOT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasAnyRole("ROOT", "ADMIN")
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .anyRequest().permitAll()
                )
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(Customizer.withDefaults())
                )
                .addFilterBefore(jwtBlacklistFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/oauth2/token/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            // Access Token 블랙리스트 추가
                            String authHeader = request.getHeader("Authorization");
                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                String accessToken = authHeader.substring(7);
                                Instant expiresAt = Instant.now().plus(Duration.ofDays(1));
                                tokenBlacklistService.blacklistToken(accessToken, expiresAt);
                            }
                            
                            // Refresh Token 블랙리스트 추가
                            String refreshToken = request.getParameter("refresh_token");
                            if (refreshToken != null) {
                                Instant expiresAt = Instant.now().plus(Duration.ofDays(7));
                                tokenBlacklistService.blacklistToken(refreshToken, expiresAt);
                            }
                        })
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new RegisteredClientRepository() {
            @Override
            public void save(RegisteredClient registeredClient) {
            }

            @Override
            public RegisteredClient findById(String id) {
                return oAuthClientService.findByIdString(id);
            }

            @Override
            public RegisteredClient findByClientId(String clientId) {
                return oAuthClientService.loadClientByClientId(clientId);
            }
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return rsaKeysService.loadByJwkSet("bomkey");
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return converter;
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder, OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
        
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        
        return new DelegatingOAuth2TokenGenerator(
            jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
