package com.example.booktracker.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // custom methods
    public Optional<User> findByUsername(String userName);

    public Optional<User> findByEmail(String email);

}
