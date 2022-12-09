package com.numo.server.mappers;

import com.numo.proto.GetUserResponse;
import com.numo.proto.UpdateUserRequest;
import com.numo.proto.UpdateUserResponse;
import com.numo.server.db.entities.User;
import com.numo.server.models.UpdateUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    public GetUserResponse mapToGetUserResponse(User from) {
        final GetUserResponse.Builder builder = GetUserResponse.newBuilder()
                .setEmail(from.getEmail())
                .setName(from.getName())
                .setPhysicalFitness(from.getPhysicalFitness());
        Optional.ofNullable(from.getGender()).ifPresent(builder::setGender);
        Optional.ofNullable(from.getAge()).ifPresent(builder::setAge);
        Optional.ofNullable(from.getWeight()).ifPresent(builder::setWeight);
        Optional.ofNullable(from.getProfileImageUrl()).ifPresent(builder::setProfileImageUrl);
        return builder.build();
    }

    public UpdateUserResponse mapToUpdateUserResponse(User from) {
        final UpdateUserResponse.Builder builder = UpdateUserResponse.newBuilder()
                .setEmail(from.getEmail())
                .setName(from.getName())
                .setPhysicalFitness(from.getPhysicalFitness());
        Optional.ofNullable(from.getGender()).ifPresent(builder::setGender);
        Optional.ofNullable(from.getAge()).ifPresent(builder::setAge);
        Optional.ofNullable(from.getWeight()).ifPresent(builder::setWeight);
        Optional.ofNullable(from.getProfileImageUrl()).ifPresent(builder::setProfileImageUrl);
        return builder.build();
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
