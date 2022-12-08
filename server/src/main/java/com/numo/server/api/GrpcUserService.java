package com.numo.server.api;

import com.numo.proto.GetUserRequest;
import com.numo.proto.GetUserResponse;
import com.numo.proto.UserServiceGrpc;
import com.numo.server.mappers.UserMapper;
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
        userService.findById(userId).map(userMapper::map).ifPresent(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
