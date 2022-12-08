package com.numo.server.services.impl;

import com.numo.proto.SignInRequest;
import com.numo.proto.SignInResponse;
import com.numo.proto.VerifyEmailRequest;
import com.numo.proto.VerifyEmailResponse;
import com.numo.server.models.CreateUser;
import com.numo.server.properties.CognitoProperties;
import com.numo.server.services.CognitoService;
import com.numo.server.services.UserService;
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
public class CognitoServiceImpl implements CognitoService {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private final CognitoIdentityProviderClient client;
    private final CognitoProperties properties;
    private final UserService userService;

    @Override
    public com.numo.proto.SignUpResponse signUp(com.numo.proto.SignUpRequest request) {
        final SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(request.getEmail())
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getEmail()))
                .password(request.getPassword())
                .build();

        final SignUpResponse response = client.signUp(signUpRequest);
        log.info("SignUpResponse: {}", response);

        final CreateUser createUserRequest = CreateUser.builder()
                .id(response.userSub())
                .email(request.getEmail())
                .build();
        userService.create(createUserRequest);
        return com.numo.proto.SignUpResponse.newBuilder().build();
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {
        final ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .username(request.getEmail())
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getEmail()))
                .confirmationCode(request.getConfirmationCode())
                .build();

        final ConfirmSignUpResponse response = client.confirmSignUp(confirmSignUpRequest);
        log.info("ConfirmSignUpResponse: {}", response);
        return VerifyEmailResponse.newBuilder().build();
    }

    @Override
    public com.numo.proto.ResendConfirmationCodeResponse resendConfirmationCode(com.numo.proto.ResendConfirmationCodeRequest request) {
        final ResendConfirmationCodeRequest resendConfirmationCodeRequest = ResendConfirmationCodeRequest.builder()
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getEmail()))
                .username(request.getEmail())
                .build();
        final ResendConfirmationCodeResponse response = client.resendConfirmationCode(resendConfirmationCodeRequest);
        log.info("ResendConfirmationCodeResponse: {}", response);
        return com.numo.proto.ResendConfirmationCodeResponse.newBuilder().build();
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        final Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", request.getEmail());
        authParams.put("PASSWORD", request.getPassword());
        authParams.put("SECRET_HASH", calculateSecretHash(request.getEmail()));

        final InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .clientId(properties.getClientId())
                .build();

        final InitiateAuthResponse response = client.initiateAuth(initiateAuthRequest);
        log.info("InitiateAuthResponse: {}", response);
        return SignInResponse.newBuilder()
                .setAccessToken(response.authenticationResult().accessToken())
                .setExpiresIn(response.authenticationResult().expiresIn())
                .setTokenType(response.authenticationResult().tokenType())
                .setRefreshToken(response.authenticationResult().refreshToken())
                .build();
    }

    @Override
    public com.numo.proto.ForgotPasswordResponse forgotPassword(com.numo.proto.ForgotPasswordRequest request) {
        final ForgotPasswordRequest forgotPasswordRequest = ForgotPasswordRequest.builder()
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getEmail()))
                .username(request.getEmail())
                .build();
        final ForgotPasswordResponse response = client.forgotPassword(forgotPasswordRequest);
        log.info("ForgotPasswordResponse: {}", response);
        return com.numo.proto.ForgotPasswordResponse.newBuilder().build();
    }

    @Override
    public com.numo.proto.ConfirmForgotPasswordResponse confirmForgotPassword(com.numo.proto.ConfirmForgotPasswordRequest request) {
        final ConfirmForgotPasswordRequest confirmForgotPasswordRequest = ConfirmForgotPasswordRequest.builder()
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(request.getEmail()))
                .username(request.getEmail())
                .confirmationCode(request.getConfirmationCode())
                .password(request.getPassword())
                .build();
        final ConfirmForgotPasswordResponse response = client.confirmForgotPassword(confirmForgotPasswordRequest);
        log.info("ConfirmForgotPasswordResponse: {}", response);
        return com.numo.proto.ConfirmForgotPasswordResponse.newBuilder().build();
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
