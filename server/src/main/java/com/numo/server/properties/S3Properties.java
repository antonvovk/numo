package com.numo.server.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@ConstructorBinding
@Validated
@ConfigurationProperties(prefix = "s3")
@RequiredArgsConstructor
@Getter
public class S3Properties {

    @NonNull
    private final String bucketName;

    @NonNull
    private final String accessPointUrl;
}
