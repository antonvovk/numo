package com.numo.server.configuration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.numo.server.properties.CognitoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {

    private final CognitoProperties cognitoProperties;

    @Bean
    AuthenticationProvider authenticationProvider() throws MalformedURLException {
        return new JwtAuthenticationProvider(jwtDecoder());
    }

    @Bean
    JwtDecoder jwtDecoder() throws MalformedURLException {
        final NimbusJwtDecoder nimbusJwtDecoder = new NimbusJwtDecoder(jwtProcessor());
        nimbusJwtDecoder.setJwtValidator(oAuth2TokenValidator());
        return nimbusJwtDecoder;
    }

    @Bean
    ConfigurableJWTProcessor<SecurityContext> jwtProcessor() throws MalformedURLException {
        final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(keySelector());
        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(cognitoProperties.getIssuer()).build(),
                new HashSet<>(Arrays.asList("sub", "client_id", "exp", "iat", "jti", "username"))
        ));
        return jwtProcessor;
    }

    @Bean
    JWSKeySelector<SecurityContext> keySelector() throws MalformedURLException {
        return new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource());
    }

    @Bean
    JWKSource<SecurityContext> keySource() throws MalformedURLException {
        return new RemoteJWKSet<>(new URL(cognitoProperties.getJwk()));
    }

    @Bean
    OAuth2TokenValidator<Jwt> oAuth2TokenValidator() {
        return new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                new JwtIssuerValidator(cognitoProperties.getIssuer()),
                new JwtClaimValidator<>("client_id", value -> cognitoProperties.getClientId().equals(value))
        );
    }
}
