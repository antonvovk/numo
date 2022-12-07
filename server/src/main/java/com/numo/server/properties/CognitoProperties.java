package com.numo.server.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@ConstructorBinding
@Validated
@ConfigurationProperties(prefix = "cognito")
@RequiredArgsConstructor
@Getter
public class CognitoProperties {

    @NonNull
    private final String userPoolId;

    @NonNull
    private final String clientId;

    @NonNull
    private final String clientSecret;

    @NonNull
    private final String issuer;

    @NonNull
    private final String jwk;
}
