package com.numo.server.api;

import com.numo.proto.*;
import com.numo.server.services.CognitoService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@RequiredArgsConstructor
@GrpcService
public class GrpcAuthenticationService extends AuthenticationServiceGrpc.AuthenticationServiceImplBase {

    private final CognitoService cognitoService;

    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        responseObserver.onNext(cognitoService.signUp(request));
        responseObserver.onCompleted();
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request, StreamObserver<VerifyEmailResponse> responseObserver) {
        responseObserver.onNext(cognitoService.verifyEmail(request));
        responseObserver.onCompleted();
    }

    @Override
    public void resendConfirmationCode(ResendConfirmationCodeRequest request, StreamObserver<ResendConfirmationCodeResponse> responseObserver) {
        responseObserver.onNext(cognitoService.resendConfirmationCode(request));
        responseObserver.onCompleted();
    }

    @Override
    public void signIn(SignInRequest request, StreamObserver<SignInResponse> responseObserver) {
        responseObserver.onNext(cognitoService.signIn(request));
        responseObserver.onCompleted();
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
        responseObserver.onNext(cognitoService.refreshToken(request));
        responseObserver.onCompleted();
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request, StreamObserver<ForgotPasswordResponse> responseObserver) {
        responseObserver.onNext(cognitoService.forgotPassword(request));
        responseObserver.onCompleted();
    }

    @Override
    public void confirmForgotPassword(ConfirmForgotPasswordRequest request, StreamObserver<ConfirmForgotPasswordResponse> responseObserver) {
        responseObserver.onNext(cognitoService.confirmForgotPassword(request));
        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        responseObserver.onNext(cognitoService.changePassword(request));
        responseObserver.onCompleted();
    }

    @Override
    public void confirmChangePassword(ConfirmChangePasswordRequest request, StreamObserver<ConfirmChangePasswordResponse> responseObserver) {
        responseObserver.onNext(cognitoService.confirmChangePassword(request));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        responseObserver.onNext(cognitoService.deleteUser(request));
        responseObserver.onCompleted();
    }
}
