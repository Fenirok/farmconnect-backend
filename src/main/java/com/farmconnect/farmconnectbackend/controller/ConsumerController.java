package com.farmconnect.farmconnectbackend.controller;

import com.farmconnect.farmconnectbackend.model.ConsumerProfile;
import com.farmconnect.farmconnectbackend.model.User;
import com.farmconnect.farmconnectbackend.repository.ConsumerProfileRepository;
import com.farmconnect.farmconnectbackend.repository.UserRepository;
import com.farmconnect.farmconnectbackend.repository.FarmerProfileRepository;
import com.farmconnect.farmconnectbackend.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consumer")
public class ConsumerController {
    @Autowired
    private ConsumerProfileRepository consumerProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FarmerProfileRepository farmerProfileRepository;
    @Autowired
    private ConsumerService consumerService;

    @PostMapping("/profile")
    public ResponseEntity<?> completeConsumerProfile(@Valid @RequestBody ConsumerProfileRequest request, BindingResult result) {
        return consumerService.completeConsumerProfile(request, result);
    }

    @GetMapping("/profile")
    public ResponseEntity<List<ConsumerProfile>> getAllProfiles() {
        return ResponseEntity.ok(consumerProfileRepository.findAll());
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ConsumerProfile> getProfileById(@PathVariable long id) {
        return consumerProfileRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DTO for request
    public static class ConsumerProfileRequest {
        @NotNull(message = "User ID is required")
        private Long userId;
        @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar must be a 12-digit number")
        private String aadharNo;
        @Pattern(regexp = "(^$|^[A-Z]{5}[0-9]{4}[A-Z]{1}$)", message = "Invalid PAN format")
        private String panNo;
        @NotBlank(message = "State is required")
        private String state;
        private String city;
        private String address;
        @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be a 6-digit number")
        private String pincode;
        private String nearbyLandmark;
        private String preferences;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getAadharNo() { return aadharNo; }
        public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }
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
        public String getNearbyLandmark() { return nearbyLandmark; }
        public void setNearbyLandmark(String nearbyLandmark) { this.nearbyLandmark = nearbyLandmark; }
        public String getPreferences() { return preferences; }
        public void setPreferences(String preferences) { this.preferences = preferences; }
    }
}
