package com.Six_sem_project.PSR;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Six_sem_project.PSR.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndMode(String email, String mode);
    Optional<User> findByUsernameAndMode(String username, String mode);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByUsername(String username);
}


