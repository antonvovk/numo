package com.numo.server.exceptions;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public Status handleInvalidArgument(IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e);
    }

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public Status handleEntityNotFoundException(EntityNotFoundException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
    }
}
