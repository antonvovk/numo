package com.numo.server.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConstructorBinding
@Validated
@ConfigurationProperties(prefix = "cognito")
@RequiredArgsConstructor
@Getter
public class CognitoProperties {

    private final String userPoolId;
    private final String clientId;
    private final String clientSecret;
}
