package com.example.booktracker.user.repository;

import com.example.booktracker.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // custom methods
    Optional<User> findByUsername(String userName);
    Optional<User> findByEmail(String email);
    void deleteByUsername(String username);
}
