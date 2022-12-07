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
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.signUp(request));
        responseObserver.onCompleted();
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request, StreamObserver<VerifyEmailResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.verifyEmail(request));
        responseObserver.onCompleted();
    }

    @Override
    public void signIn(SignInRequest request, StreamObserver<SignInResponse> responseObserver) {
        responseObserver.onNext(cognitoClient.signIn(request));
        responseObserver.onCompleted();
    }
}
