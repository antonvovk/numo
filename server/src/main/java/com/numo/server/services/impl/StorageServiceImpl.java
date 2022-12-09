package com.numo.server.services.impl;

import com.numo.server.properties.S3Properties;
import com.numo.server.services.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final S3Properties properties;

    @Override
    public String putObject(String name, byte[] content) {
        final PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(name)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        final PutObjectResponse response = s3Client.putObject(objectRequest, RequestBody.fromBytes(content));
        log.info("PutObjectResponse: {}", response);
        return properties.getAccessPointUrl() + "/" + name;
    }
}
