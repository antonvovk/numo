package com.numo.server.services.impl;

import com.numo.server.db.entities.User;
import com.numo.server.db.repositories.UserRepository;
import com.numo.server.exceptions.UserEntityNotFoundException;
import com.numo.server.models.CreateUser;
import com.numo.server.models.UpdateUser;
import com.numo.server.services.StorageService;
import com.numo.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.numo.server.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public User getUser() {
        final String id = getCurrentUserId();
        return repository.findById(id).orElseThrow(() -> UserEntityNotFoundException.of(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserEmail() {
        final String id = getCurrentUserId();
        return repository.findEmailById(id).orElseThrow(() -> UserEntityNotFoundException.of(id));
    }

    @Override
    @Transactional
    public User create(CreateUser request) {
        final User user = User.builder().id(request.id()).email(request.email()).build();
        return repository.save(user);
    }

    @Override
    @Transactional
    public User update(UpdateUser request) {
        final User user = getUser();
        user.setName(request.name());
        user.setGender(request.gender());
        user.setAge(request.age());
        user.setWeight(request.weight());
        user.setPhysicalFitness(request.physicalFitness());
        return repository.save(user);
    }

    @Override
    @Transactional
    public String changeProfileImage(String imageType, byte[] image) {
        final User user = getUser();

        final String filename = user.getId() + "." + imageType;
        final String profileImageUrl = storageService.putObject(filename, image);

        user.setProfileImageUrl(profileImageUrl);
        return repository.save(user).getProfileImageUrl();
    }

    @Override
    @Transactional
    public void delete() {
        repository.delete(getUser());
    }
}
