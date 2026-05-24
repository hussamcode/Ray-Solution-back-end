package com.example.RaySolution.Repository;

import com.example.RaySolution.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);


    Optional<User> findByVerificationCode(String verificationCode);
}
