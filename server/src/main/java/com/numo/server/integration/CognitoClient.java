package com.numo.server.integration;

import com.numo.proto.*;

public interface CognitoClient {

    SendConfirmationCodeResponse sendConfirmationCode(SendConfirmationCodeRequest request);

    VerifyPhoneNumberResponse verifyPhoneNumber(VerifyPhoneNumberRequest request);

    SignUpResponse signUp(SignUpRequest request);

    SignInResponse signIn(SignInRequest request);
}
