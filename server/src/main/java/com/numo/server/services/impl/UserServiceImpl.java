package com.numo.server.services.impl;

import com.numo.server.db.entities.User;
import com.numo.server.db.repositories.UserRepository;
import com.numo.server.exceptions.EntityNotFoundException;
import com.numo.server.models.CreateUser;
import com.numo.server.models.UpdateUser;
import com.numo.server.services.StorageService;
import com.numo.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findEmailById(String id) {
        return repository.findEmailById(id);
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
        final User user = repository.findById(request.id())
                .orElseThrow(() -> EntityNotFoundException.of(request.id(), User.class));
        user.setName(request.name());
        user.setGender(request.gender());
        user.setAge(request.age());
        user.setWeight(request.weight());
        user.setPhysicalFitness(request.physicalFitness());
        return repository.save(user);
    }

    @Override
    @Transactional
    public String changeProfileImage(String id, String imageType, byte[] image) {
        final User user = repository.findById(id).orElseThrow(() -> EntityNotFoundException.of(id, User.class));

        final String filename = id + "." + imageType;
        final String profileImageUrl = storageService.putObject(filename, image);

        user.setProfileImageUrl(profileImageUrl);
        return repository.save(user).getProfileImageUrl();
    }
}
