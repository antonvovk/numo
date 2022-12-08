package com.numo.server.mappers;

import com.numo.proto.GetUserResponse;
import com.numo.server.db.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public GetUserResponse map(User from) {
        return GetUserResponse.newBuilder()
                .setEmail(from.getEmail())
                .setName(from.getName())
                .setGender(from.getGender())
                .setAge(from.getAge())
                .setWeight(from.getWeight())
                .setPhysicalFitness(from.getPhysicalFitness())
                .build();
    }
}
