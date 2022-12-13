package com.numo.server.services;

import com.numo.proto.*;

public interface CognitoService {

    TokenResponse signUp(EmailAndPasswordRequest request);

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

    ResendConfirmationCodeResponse resendConfirmationCode(ResendConfirmationCodeRequest request);

    TokenResponse signIn(EmailAndPasswordRequest request);

    TokenResponse refreshToken(RefreshTokenRequest request);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    ConfirmForgotPasswordResponse confirmForgotPassword(ConfirmForgotPasswordRequest request);

    ChangePasswordResponse changePassword(ChangePasswordRequest request);

    ConfirmChangePasswordResponse confirmChangePassword(ConfirmChangePasswordRequest request);

    DeleteUserResponse deleteUser(DeleteUserRequest request);
}
