package com.numo.server.services;

import com.numo.proto.*;

public interface CognitoService {

    SignUpResponse signUp(SignUpRequest request);

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

    ResendConfirmationCodeResponse resendConfirmationCode(ResendConfirmationCodeRequest request);

    SignInResponse signIn(SignInRequest request);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    ConfirmForgotPasswordResponse confirmForgotPassword(ConfirmForgotPasswordRequest request);
}
