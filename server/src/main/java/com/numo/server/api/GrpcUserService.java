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
        final User user = userService.getUser();
        final GetUserResponse response = userMapper.mapToGetUserResponse(user);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        final UpdateUser updateUserRequest = userMapper.mapToUpdateUser(request);
        final User user = userService.update(updateUserRequest);
        final UpdateUserResponse response = userMapper.mapToUpdateUserResponse(user);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void changeProfileImage(ChangeProfileImageRequest request, StreamObserver<ChangeProfileImageResponse> responseObserver) {
        final String imageType = request.getMetadata().getType();
        final byte[] image = request.getFile().getContent().toByteArray();
        final String profileImageUrl = userService.changeProfileImage(imageType, image);
        final ChangeProfileImageResponse response = ChangeProfileImageResponse.newBuilder()
                .setProfileImageUrl(profileImageUrl)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
