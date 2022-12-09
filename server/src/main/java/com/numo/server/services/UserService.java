package com.numo.server.services;

import com.numo.server.db.entities.User;
import com.numo.server.models.CreateUser;
import com.numo.server.models.UpdateUser;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(String id);

    Optional<String> findEmailById(String id);

    User create(CreateUser request);

    User update(UpdateUser request);

    String changeProfileImage(String id, String imageType, byte[] image);
}
