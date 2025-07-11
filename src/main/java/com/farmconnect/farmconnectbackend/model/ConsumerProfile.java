package com.farmconnect.farmconnectbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consumer_profiles")
public class ConsumerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "aadhar_no")
    private String aadharNo;

    @Column(name = "pan_no")
    private String panNo;

    private String state;
    private String city;
    private String address;
    private String pincode;

    @Column(name = "nearby_landmark")
    private String nearbyLandmark;

    private String preferences;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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