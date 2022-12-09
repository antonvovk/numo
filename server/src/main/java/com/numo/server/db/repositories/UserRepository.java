package com.numo.server.db.repositories;

import com.numo.server.db.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT user.email FROM User user WHERE user.id = :id")
    Optional<String> findEmailById(String id);
}
