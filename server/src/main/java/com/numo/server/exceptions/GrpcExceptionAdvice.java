package com.numo.server.exceptions;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public Status handleEntityNotFoundException(EntityNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(InvalidParameterException.class)
    public Status handleInvalidParameterException(InvalidParameterException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.awsErrorDetails().errorMessage());
    }

    @GrpcExceptionHandler(NotAuthorizedException.class)
    public Status handleNotAuthorizedException(NotAuthorizedException e) {
        return Status.UNAUTHENTICATED.withDescription(e.awsErrorDetails().errorMessage());
    }

    @GrpcExceptionHandler(CodeDeliveryFailureException.class)
    public Status handleCodeDeliveryFailureException(CodeDeliveryFailureException e) {
        return Status.FAILED_PRECONDITION.withDescription(e.awsErrorDetails().errorMessage());
    }

    @GrpcExceptionHandler(UsernameExistsException.class)
    public Status handleUsernameExistsException(UsernameExistsException e) {
        return Status.ALREADY_EXISTS.withDescription(e.awsErrorDetails().errorMessage());
    }

    @GrpcExceptionHandler(InvalidPasswordException.class)
    public Status handleInvalidPasswordException(InvalidPasswordException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.awsErrorDetails().errorMessage());
    }

    @GrpcExceptionHandler(ExpiredCodeException.class)
    public Status handleExpiredCodeException(ExpiredCodeException e) {
        return Status.DEADLINE_EXCEEDED.withDescription(e.awsErrorDetails().errorMessage());
    }

    @GrpcExceptionHandler(UserNotFoundException.class)
    public Status handleUserNotFoundException(UserNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.awsErrorDetails().errorMessage());
    }
}
