package com.numo.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfiguration {

    private static final Region REGION = Region.EU_NORTH_1;

    @Bean
    CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(REGION)
                .build();
    }

    @Bean
    S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(REGION)
                .build();
    }
}
