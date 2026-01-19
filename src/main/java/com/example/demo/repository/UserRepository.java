package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
            SELECT u FROM User u JOIN FETCH u.role WHERE u.email=:email
            """)
    Optional<User> findByUsername(@Param("email") String email);

    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);

}
