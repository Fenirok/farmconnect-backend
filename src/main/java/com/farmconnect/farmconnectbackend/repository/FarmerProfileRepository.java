package com.farmconnect.farmconnectbackend.repository;

import com.farmconnect.farmconnectbackend.model.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
} 