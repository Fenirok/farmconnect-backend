package com.farmconnect.farmconnectbackend.controller;

import com.farmconnect.farmconnectbackend.model.FarmerProfile;
import com.farmconnect.farmconnectbackend.model.User;
import com.farmconnect.farmconnectbackend.repository.FarmerProfileRepository;
import com.farmconnect.farmconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import com.farmconnect.farmconnectbackend.repository.ConsumerProfileRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {
    @Autowired
    private FarmerProfileRepository farmerProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConsumerProfileRepository consumerProfileRepository;

    @PostMapping("/profile")
    public ResponseEntity<?> completeFarmerProfile(@Valid @RequestBody FarmerProfileRequest request, BindingResult result) {
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

    @GetMapping("/profile")
    public ResponseEntity<List<FarmerProfile>> getAllProfiles() {
        return ResponseEntity.ok(farmerProfileRepository.findAll());
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<FarmerProfile> getProfileById(@PathVariable long id) {
        return farmerProfileRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DTO for request
    public static class FarmerProfileRequest {
        @NotNull(message = "User ID is required")
        private Long userId;
        @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar must be a 12-digit number")
        private String aadharNo;
        private String kisanId;
        @Pattern(regexp = "(^$|^[A-Z]{5}[0-9]{4}[A-Z]{1}$)", message = "Invalid PAN format")
        private String panNo;
        @NotBlank(message = "State is required")
        private String state;
        private String city;
        private String address;
        @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be a 6-digit number")
        private String pincode;
        private String farmLocation;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getAadharNo() { return aadharNo; }
        public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }
        public String getKisanId() { return kisanId; }
        public void setKisanId(String kisanId) { this.kisanId = kisanId; }
        public String getPanNo() { return panNo; }
        public void setPanNo(String panNo) { this.panNo = panNo; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }
        public String getFarmLocation() { return farmLocation; }
        public void setFarmLocation(String farmLocation) { this.farmLocation = farmLocation; }
    }
} 