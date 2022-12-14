package com.numo.server.services;

import com.numo.server.db.entities.User;
import com.numo.server.models.CreateUser;
import com.numo.server.models.UpdateUser;

public interface UserService {

    User getUser();

    String getUserEmail();

    User create(CreateUser request);

    User update(UpdateUser request);

    String changeProfileImage(String imageType, byte[] image);

    void delete();
}
