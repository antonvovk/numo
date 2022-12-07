package com.numo.server.services;

import com.numo.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        final GetUserResponse response = GetUserResponse.newBuilder()
                .setEmail("antonn.vovk@gmail.com")
                .setName("Anton Vovk")
                .setGender(Gender.MALE)
                .setAge(22)
                .setWeight(77)
                .setPhysicalFitness(PhysicalFitness.MODERATE)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
