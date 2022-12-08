package com.numo.server.services.impl;

import com.numo.server.db.entities.User;
import com.numo.server.db.repositories.UserRepository;
import com.numo.server.models.CreateUser;
import com.numo.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public User create(CreateUser request) {
        final User user = User.builder().id(request.id()).email(request.email()).build();
        return repository.save(user);
    }
}
