package com.numo.server.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class EntityNotFoundException extends RuntimeException {

    private final String id;
    private final Class<?> clazz;

    @Override
    public String getMessage() {
        return "Entity " + clazz.getSimpleName() + " with id " + id + " not found.";
    }
}
