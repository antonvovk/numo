package com.numo.server.integration;

import com.numo.proto.*;

public interface CognitoClient {

    SignUpResponse signUp(SignUpRequest request);

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

    SignInResponse signIn(SignInRequest request);
}
