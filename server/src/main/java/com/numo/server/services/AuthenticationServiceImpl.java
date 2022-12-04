package com.numo.server.services;

import com.numo.proto.*;
import com.numo.server.integration.CognitoClient;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@RequiredArgsConstructor
@GrpcService
public class AuthenticationServiceImpl extends AuthenticationServiceGrpc.AuthenticationServiceImplBase {

    private final CognitoClient cognitoClient;

    @Override
    public void sendConfirmationCode(SendConfirmationCodeRequest request, StreamObserver<SendConfirmationCodeResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.sendConfirmationCode(request));
        responseObserver.onCompleted();
    }

    @Override
    public void verifyPhoneNumber(VerifyPhoneNumberRequest request, StreamObserver<VerifyPhoneNumberResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.verifyPhoneNumber(request));
        responseObserver.onCompleted();
    }

    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.signUp(request));
        responseObserver.onCompleted();
    }

    @Override
    public void signIn(SignInRequest request, StreamObserver<SignInResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.signIn(request));
        responseObserver.onCompleted();
    }
}
