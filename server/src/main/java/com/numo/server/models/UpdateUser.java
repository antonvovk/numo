package com.numo.server.models;

import com.numo.proto.Gender;
import com.numo.proto.PhysicalFitness;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateUser(String name,
                         Gender gender,
                         Integer age,
                         Integer weight,
                         PhysicalFitness physicalFitness) {
}
