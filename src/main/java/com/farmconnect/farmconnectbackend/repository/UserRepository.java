package com.farmconnect.farmconnectbackend.repository;

import com.farmconnect.farmconnectbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
} 