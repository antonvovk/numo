package com.numo.server.mappers;

import com.numo.proto.GetUserResponse;
import com.numo.proto.UpdateUserRequest;
import com.numo.proto.UpdateUserResponse;
import com.numo.server.db.entities.User;
import com.numo.server.models.UpdateUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public GetUserResponse mapToGetUserResponse(User from) {
        return GetUserResponse.newBuilder()
                .setEmail(from.getEmail())
                .setName(from.getName())
                .setGender(from.getGender())
                .setAge(from.getAge())
                .setWeight(from.getWeight())
                .setPhysicalFitness(from.getPhysicalFitness())
                .build();
    }

    public UpdateUserResponse mapToUpdateUserResponse(User from) {
        return UpdateUserResponse.newBuilder()
                .setEmail(from.getEmail())
                .setName(from.getName())
                .setGender(from.getGender())
                .setAge(from.getAge())
                .setWeight(from.getWeight())
                .setPhysicalFitness(from.getPhysicalFitness())
                .build();
    }

    public UpdateUser mapToUpdateUser(UpdateUserRequest from) {
        return UpdateUser.builder()
                .name(from.getName())
                .gender(from.getGender())
                .age(from.getAge())
                .weight(from.getWeight())
                .physicalFitness(from.getPhysicalFitness())
                .build();
    }
}
