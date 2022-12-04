package com.numo.server.integration;

import com.numo.proto.SendConfirmationCodeRequest;
import com.numo.proto.SendConfirmationCodeResponse;
import com.numo.proto.VerifyPhoneNumberRequest;
import com.numo.proto.VerifyPhoneNumberResponse;
import com.numo.server.properties.CognitoProperties;
import com.numo.server.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class CognitoClientImpl implements CognitoClient {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private final CognitoIdentityProviderClient client;
    private final CognitoProperties properties;

    @Override
    public SendConfirmationCodeResponse sendConfirmationCode(SendConfirmationCodeRequest request) {
        final SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(request.getPhoneNumber())
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getPhoneNumber()))
                .password(PasswordUtils.generate())
                .userAttributes(AttributeType.builder().name("phone_number").value(request.getPhoneNumber()).build())
                .build();
        final SignUpResponse response = client.signUp(signUpRequest);
        log.info("SignUpResponse: {}", response);
        return SendConfirmationCodeResponse.newBuilder().build();
    }

    @Override
    public VerifyPhoneNumberResponse verifyPhoneNumber(VerifyPhoneNumberRequest request) {
        final ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .username(request.getPhoneNumber())
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getPhoneNumber()))
                .confirmationCode(request.getConfirmationCode())
                .build();
        final ConfirmSignUpResponse response = client.confirmSignUp(confirmSignUpRequest);
        log.info("ConfirmSignUpResponse: {}", response);
        return VerifyPhoneNumberResponse.newBuilder().build();
    }

    @Override
    public com.numo.proto.SignUpResponse signUp(com.numo.proto.SignUpRequest request) {
        adminSetUserPassword(request);
        adminUpdateUserAttributes(request);
        return com.numo.proto.SignUpResponse.newBuilder().build();
    }

    private void adminSetUserPassword(com.numo.proto.SignUpRequest request) {
        final AdminSetUserPasswordRequest adminSetUserPasswordRequest = AdminSetUserPasswordRequest.builder()
                .userPoolId(properties.getUserPoolId())
                .username(request.getPhoneNumber())
                .password(request.getPassword())
                .permanent(Boolean.TRUE)
                .build();
        final AdminSetUserPasswordResponse response = client.adminSetUserPassword(adminSetUserPasswordRequest);
        log.info("AdminSetUserPasswordResponse: {}", response);
    }

    private void adminUpdateUserAttributes(com.numo.proto.SignUpRequest request) {
        final AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = AdminUpdateUserAttributesRequest.builder()
                .userPoolId(properties.getUserPoolId())
                .username(request.getPhoneNumber())
                .userAttributes(
                        AttributeType.builder().name("custom:gender").value(request.getGender().toString()).build(),
                        AttributeType.builder().name("custom:weight").value(String.valueOf(request.getWeight())).build(),
                        AttributeType.builder().name("custom:physical_fitness").value(request.getPhysicalFitness().toString()).build()
                )
                .build();
        final AdminUpdateUserAttributesResponse response = client.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);
        log.info("AdminUpdateUserAttributesResponse: {}", response);
    }

    @Override
    public com.numo.proto.SignInResponse signIn(com.numo.proto.SignInRequest request) {
        final Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", request.getPhoneNumber());
        authParams.put("PASSWORD", request.getPassword());
        authParams.put("SECRET_HASH", calculateSecretHash(request.getPhoneNumber()));
        final InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .clientId(properties.getClientId())
                .build();
        final InitiateAuthResponse response = client.initiateAuth(initiateAuthRequest);
        log.info("InitiateAuthResponse: {}", response);
        return com.numo.proto.SignInResponse.newBuilder()
                .setAccessToken(response.authenticationResult().accessToken())
                .setExpiresIn(response.authenticationResult().expiresIn())
                .setTokenType(response.authenticationResult().tokenType())
                .setRefreshToken(response.authenticationResult().refreshToken())
                .setIdToken(response.authenticationResult().idToken())
                .build();
    }

    private String calculateSecretHash(String username) {
        final SecretKeySpec key = new SecretKeySpec(properties.getClientSecret().getBytes(UTF_8), HMAC_SHA256_ALGORITHM);
        try {
            final Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(key);
            mac.update(username.getBytes(UTF_8));
            byte[] rawHmac = mac.doFinal(properties.getClientId().getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException while calculating secret hash", e);
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException while calculating secret hash", e);
        }
        return null;
    }
}
