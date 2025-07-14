package com.farmconnect.farmconnectbackend.controller;

import com.farmconnect.farmconnectbackend.model.FarmerProfile;
import com.farmconnect.farmconnectbackend.model.ConsumerProfile;
import com.farmconnect.farmconnectbackend.model.User;
import com.farmconnect.farmconnectbackend.repository.FarmerProfileRepository;
import com.farmconnect.farmconnectbackend.repository.ConsumerProfileRepository;
import com.farmconnect.farmconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private FarmerProfileRepository farmerProfileRepository;
    @Autowired
    private ConsumerProfileRepository consumerProfileRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/farmer")
    public ResponseEntity<?> completeFarmerProfile(@RequestBody FarmerProfileRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        FarmerProfile profile = farmerProfileRepository.findByUser(userOpt.get()).orElse(new FarmerProfile());
        profile.setUser(userOpt.get());
        profile.setAadharNo(request.getAadharNo());
        profile.setKisanId(request.getKisanId());
        profile.setPanNo(request.getPanNo());
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setPincode(request.getPincode());
        profile.setFarmLocation(request.getFarmLocation());
        farmerProfileRepository.save(profile);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/consumer")
    public ResponseEntity<?> completeConsumerProfile(@RequestBody ConsumerProfileRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        ConsumerProfile profile = consumerProfileRepository.findByUser(userOpt.get()).orElse(new ConsumerProfile());
        profile.setUser(userOpt.get());
        profile.setAadharNo(request.getAadharNo());
        profile.setPanNo(request.getPanNo());
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setPincode(request.getPincode());
        profile.setNearbyLandmark(request.getNearbyLandmark());
        profile.setPreferences(request.getPreferences());
        consumerProfileRepository.save(profile);
        return ResponseEntity.ok(profile);
    }

    // DTOs for requests
    public static class FarmerProfileRequest {
        private Long userId;
        private String aadharNo;
        private String kisanId;
        private String panNo;
        private String state;
        private String city;
        private String address;
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

    public static class ConsumerProfileRequest {
        private Long userId;
        private String aadharNo;
        private String panNo;
        private String state;
        private String city;
        private String address;
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