package com.farmconnect.farmconnectbackend.repository;

import com.farmconnect.farmconnectbackend.model.ConsumerProfile;
import com.farmconnect.farmconnectbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConsumerProfileRepository extends JpaRepository<ConsumerProfile, Long> {
    Optional<ConsumerProfile> findByUser(User user);
} 