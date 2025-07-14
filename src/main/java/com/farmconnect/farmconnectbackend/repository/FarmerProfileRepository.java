package com.farmconnect.farmconnectbackend.repository;

import com.farmconnect.farmconnectbackend.model.FarmerProfile;
import com.farmconnect.farmconnectbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
    Optional<FarmerProfile> findByUser(User user);
} 