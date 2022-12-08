package com.numo.server.api;

import com.numo.proto.*;
import com.numo.server.db.entities.User;
import com.numo.server.mappers.UserMapper;
import com.numo.server.models.UpdateUser;
import com.numo.server.services.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        // TODO
        final String userId = "id";
        userService.findById(userId).map(userMapper::mapToGetUserResponse).ifPresent(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        // TODO
        final String userId = "id";
        final UpdateUser updateUserRequest = userMapper.mapToUpdateUser(request).toBuilder().id(userId).build();
        final User user = userService.update(updateUserRequest);
        responseObserver.onNext(userMapper.mapToUpdateUserResponse(user));
        responseObserver.onCompleted();
    }
}
