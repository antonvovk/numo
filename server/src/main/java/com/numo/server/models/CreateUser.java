package com.numo.server.models;

import lombok.Builder;

@Builder(toBuilder = true)
public record CreateUser(String id, String email) {
}
