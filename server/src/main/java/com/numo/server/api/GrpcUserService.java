package com.numo.server.api;

import com.numo.proto.*;
import com.numo.server.db.entities.User;
import com.numo.server.exceptions.EntityNotFoundException;
import com.numo.server.mappers.UserMapper;
import com.numo.server.models.UpdateUser;
import com.numo.server.services.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import static com.numo.server.utils.SecurityUtils.getCurrentUserId;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        final String userId = getCurrentUserId();
        final GetUserResponse user = userService.findById(userId).map(userMapper::mapToGetUserResponse)
                .orElseThrow(() -> EntityNotFoundException.of(userId, User.class));
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        final String userId = getCurrentUserId();
        final UpdateUser updateUserRequest = userMapper.mapToUpdateUser(request).toBuilder().id(userId).build();
        final User user = userService.update(updateUserRequest);
        responseObserver.onNext(userMapper.mapToUpdateUserResponse(user));
        responseObserver.onCompleted();
    }

    @Override
    public void changeProfileImage(ChangeProfileImageRequest request, StreamObserver<ChangeProfileImageResponse> responseObserver) {
        final String userId = getCurrentUserId();
        final String imageType = request.getMetadata().getType();
        final byte[] image = request.getFile().getContent().toByteArray();

        final String profileImageUrl = userService.changeProfileImage(userId, imageType, image);
        final ChangeProfileImageResponse response = ChangeProfileImageResponse.newBuilder()
                .setProfileImageUrl(profileImageUrl)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
