package com.numo.server.exceptions;

import com.numo.proto.ErrorCode;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(UserEntityNotFoundException.class)
    public StatusRuntimeException handleUserEntityNotFoundException(UserEntityNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(DataIntegrityViolationException.class)
    public StatusRuntimeException handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return Status.INTERNAL.withDescription(e.getMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(InvalidParameterException.class)
    public StatusRuntimeException handleInvalidParameterException(InvalidParameterException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(NotAuthorizedException.class)
    public StatusRuntimeException handleNotAuthorizedException(NotAuthorizedException e) {
        return Status.UNAUTHENTICATED.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD));
    }

    @GrpcExceptionHandler(CodeDeliveryFailureException.class)
    public StatusRuntimeException handleCodeDeliveryFailureException(CodeDeliveryFailureException e) {
        return Status.FAILED_PRECONDITION.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(UserNotConfirmedException.class)
    public StatusRuntimeException handleUserNotConfirmedException(UserNotConfirmedException e) {
        return Status.FAILED_PRECONDITION.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(UsernameExistsException.class)
    public StatusRuntimeException handleUsernameExistsException(UsernameExistsException e) {
        return Status.ALREADY_EXISTS.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.USER_WITH_THIS_EMAIL_ALREADY_EXISTS));
    }

    @GrpcExceptionHandler(InvalidPasswordException.class)
    public StatusRuntimeException handleInvalidPasswordException(InvalidPasswordException e) {
        return Status.UNAUTHENTICATED.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(UserNotFoundException.class)
    public StatusRuntimeException handleUserNotFoundException(UserNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.UNSPECIFIED));
    }

    @GrpcExceptionHandler(ExpiredCodeException.class)
    public StatusRuntimeException handleExpiredCodeException(ExpiredCodeException e) {
        return Status.DEADLINE_EXCEEDED.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.CONFIRMATION_CODE_EXPIRED));
    }

    @GrpcExceptionHandler(CodeMismatchException.class)
    public StatusRuntimeException handleCodeMismatchException(CodeMismatchException e) {
        return Status.UNAUTHENTICATED.withDescription(e.awsErrorDetails().errorMessage())
                .asRuntimeException(metadata(ErrorCode.CONFIRMATION_CODE_MISMATCH));
    }

    private Metadata metadata(ErrorCode code) {
        final Metadata metadata = new Metadata();
        metadata.put(Key.of("error_code", Metadata.ASCII_STRING_MARSHALLER), code.toString());
        return metadata;
    }
}
