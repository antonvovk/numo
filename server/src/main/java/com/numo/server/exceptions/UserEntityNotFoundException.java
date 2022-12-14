package com.numo.server.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class UserEntityNotFoundException extends RuntimeException {

    private final String id;

    @Override
    public String getMessage() {
        return "User with id " + id + " not found.";
    }
}
