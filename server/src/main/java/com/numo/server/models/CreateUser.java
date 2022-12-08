package com.numo.server.models;

import lombok.Builder;

@Builder
public record CreateUser(String id, String email) {
}
