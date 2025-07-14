package com.farmconnect.farmconnectbackend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.farmconnectbackend.model.ConsumerProfile;
import com.farmconnect.farmconnectbackend.repository.ConsumerProfileRepository;

@Service
public class ConsumerService {

    @Autowired
    private ConsumerProfileRepository consumerRepo;

    public ConsumerProfile getProfileById(long id) {
        return consumerRepo.findById(id)
                .orElse(null);
    }

    public List<ConsumerProfile> getAllProfiles() {
        return consumerRepo.findAll();
    }

    public ConsumerProfile addCosnumer(ConsumerProfile consumerProfile) {
        return consumerRepo.save(consumerProfile);
    }

}
