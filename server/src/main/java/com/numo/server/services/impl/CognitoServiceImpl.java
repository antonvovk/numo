package com.numo.server.services.impl;

import com.numo.proto.*;
import com.numo.server.models.CreateUser;
import com.numo.server.properties.CognitoProperties;
import com.numo.server.services.CognitoService;
import com.numo.server.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResendConfirmationCodeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResendConfirmationCodeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.numo.server.utils.SecurityUtils.getCurrentAccessToken;
import static com.numo.server.utils.SecurityUtils.getCurrentUserId;
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
    public TokenResponse signUp(EmailAndPasswordRequest request) {
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

        updateUserConfirmationStatus(request.getEmail());
        return signIn(request);
    }

    private void updateUserConfirmationStatus(String email) {
        final AdminConfirmSignUpRequest confirmSignUpRequest = AdminConfirmSignUpRequest.builder()
                .userPoolId(properties.getUserPoolId())
                .username(email)
                .build();
        client.adminConfirmSignUp(confirmSignUpRequest);
        log.info("Successfully changed status of user with email {} to CONFIRMED", email);
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {
        final ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .username(getCurrentUserEmail())
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(getCurrentUserEmail()))
                .confirmationCode(request.getConfirmationCode())
                .build();
        return tryVerifyEmail(confirmSignUpRequest);
    }

    private VerifyEmailResponse tryVerifyEmail(ConfirmSignUpRequest request) {
        try {
            client.confirmSignUp(request);
        } catch (NotAuthorizedException e) {
            log.info("Verification of email {} results in: {}", request.username(), e.awsErrorDetails().errorMessage());
            confirmUserEmail(request.username());
        }
        return VerifyEmailResponse.newBuilder().build();
    }

    private void confirmUserEmail(String email) {
        final AdminUpdateUserAttributesRequest request = AdminUpdateUserAttributesRequest.builder()
                .userPoolId(properties.getUserPoolId())
                .userAttributes(AttributeType.builder().name("email_verified").value("true").build())
                .username(email)
                .build();
        client.adminUpdateUserAttributes(request);
        log.info("Successfully changed attribute email_verified to true for user with email {}", email);
    }

    @Override
    public com.numo.proto.ResendConfirmationCodeResponse resendConfirmationCode(com.numo.proto.ResendConfirmationCodeRequest request) {
        final ResendConfirmationCodeRequest resendConfirmationCodeRequest = ResendConfirmationCodeRequest.builder()
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(getCurrentUserEmail()))
                .username(getCurrentUserEmail())
                .build();
        final ResendConfirmationCodeResponse response = client.resendConfirmationCode(resendConfirmationCodeRequest);
        log.info("ResendConfirmationCodeResponse: {}", response);
        return com.numo.proto.ResendConfirmationCodeResponse.newBuilder().build();
    }

    @Override
    public TokenResponse signIn(EmailAndPasswordRequest request) {
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
        return TokenResponse.newBuilder()
                .setAccessToken(response.authenticationResult().accessToken())
                .setExpiresIn(response.authenticationResult().expiresIn())
                .setTokenType(response.authenticationResult().tokenType())
                .setRefreshToken(response.authenticationResult().refreshToken())
                .build();
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        final Map<String, String> authParams = new HashMap<>();
        authParams.put("REFRESH_TOKEN", request.getRefreshToken());
        authParams.put("SECRET_HASH", calculateSecretHash(getCurrentUserId()));

        final InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                .authParameters(authParams)
                .clientId(properties.getClientId())
                .build();

        final InitiateAuthResponse response = client.initiateAuth(initiateAuthRequest);
        log.info("InitiateAuthResponse: {}", response);
        return TokenResponse.newBuilder()
                .setAccessToken(response.authenticationResult().accessToken())
                .setExpiresIn(response.authenticationResult().expiresIn())
                .setTokenType(response.authenticationResult().tokenType())
                .setRefreshToken(request.getRefreshToken())
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
                .password(request.getNewPassword())
                .build();
        final ConfirmForgotPasswordResponse response = client.confirmForgotPassword(confirmForgotPasswordRequest);
        log.info("ConfirmForgotPasswordResponse: {}", response);
        return com.numo.proto.ConfirmForgotPasswordResponse.newBuilder().build();
    }

    @Override
    public com.numo.proto.ChangePasswordResponse changePassword(com.numo.proto.ChangePasswordRequest request) {
        final ForgotPasswordRequest forgotPasswordRequest = ForgotPasswordRequest.builder()
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(getCurrentUserEmail()))
                .username(getCurrentUserEmail())
                .build();
        final ForgotPasswordResponse response = client.forgotPassword(forgotPasswordRequest);
        log.info("ForgotPasswordResponse: {}", response);
        return com.numo.proto.ChangePasswordResponse.newBuilder().build();
    }

    @Override
    public ConfirmChangePasswordResponse confirmChangePassword(ConfirmChangePasswordRequest request) {
        final ConfirmForgotPasswordRequest confirmForgotPasswordRequest = ConfirmForgotPasswordRequest.builder()
                .clientId(properties.getClientId())
                .secretHash(calculateSecretHash(getCurrentUserEmail()))
                .username(getCurrentUserEmail())
                .confirmationCode(request.getConfirmationCode())
                .password(request.getNewPassword())
                .build();
        final ConfirmForgotPasswordResponse response = client.confirmForgotPassword(confirmForgotPasswordRequest);
        log.info("ConfirmForgotPasswordResponse: {}", response);
        return com.numo.proto.ConfirmChangePasswordResponse.newBuilder().build();
    }

    @Override
    public com.numo.proto.DeleteUserResponse deleteUser(com.numo.proto.DeleteUserRequest request) {
        final DeleteUserRequest deleteUserRequest = DeleteUserRequest.builder()
                .accessToken(getCurrentAccessToken())
                .build();
        final DeleteUserResponse response = client.deleteUser(deleteUserRequest);
        log.info("DeleteUserResponse: {}", response);

        userService.delete();
        return com.numo.proto.DeleteUserResponse.newBuilder().build();
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

    private String getCurrentUserEmail() {
        return userService.getUserEmail();
    }
}
