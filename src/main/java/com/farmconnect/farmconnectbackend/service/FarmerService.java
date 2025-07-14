package com.farmconnect.farmconnectbackend.service;

import com.farmconnect.farmconnectbackend.model.FarmerProfile;
import com.farmconnect.farmconnectbackend.model.User;
import com.farmconnect.farmconnectbackend.repository.FarmerProfileRepository;
import com.farmconnect.farmconnectbackend.repository.ConsumerProfileRepository;
import com.farmconnect.farmconnectbackend.repository.UserRepository;
import com.farmconnect.farmconnectbackend.controller.FarmerController.FarmerProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FarmerService {
    @Autowired
    private FarmerProfileRepository farmerProfileRepository;
    @Autowired
    private ConsumerProfileRepository consumerProfileRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> completeFarmerProfile(FarmerProfileRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errors);
        }
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        // Only allow users with FARMER role
        if (user.getRole() != User.Role.FARMER) {
            return ResponseEntity.status(403).body("Only users with FARMER role can create a farmer profile.");
        }
        // Prevent a user from having both profiles
        if (farmerProfileRepository.findByUser(user).isPresent()) {
            return ResponseEntity.badRequest().body("Farmer profile already exists for this user.");
        }
        if (consumerProfileRepository.findByUser(user).isPresent()) {
            return ResponseEntity.badRequest().body("User already has a consumer profile. Cannot create both profiles.");
        }
        FarmerProfile profile = new FarmerProfile();
        profile.setUser(user);
        profile.setAadharNo(request.getAadharNo());
        if (request.getKisanId() != null && !request.getKisanId().isEmpty()) {
            profile.setKisanId(request.getKisanId());
        }
        if (request.getPanNo() != null && !request.getPanNo().isEmpty()) {
            profile.setPanNo(request.getPanNo());
        }
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setPincode(request.getPincode());
        profile.setFarmLocation(request.getFarmLocation());
        farmerProfileRepository.save(profile);
        return ResponseEntity.ok(profile);
    }
} 