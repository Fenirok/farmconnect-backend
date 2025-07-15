package com.farmconnect.farmconnectbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmconnect.farmconnectbackend.model.ConsumerProfile;
import com.farmconnect.farmconnectbackend.service.ConsumerService;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/profile")
    public ResponseEntity<List<ConsumerProfile>> getAllProfiles() {
        return new ResponseEntity<>(consumerService.getAllProfiles(), HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ConsumerProfile> getProfileById(@PathVariable long id) {
        System.out.println("Profile page");
        ConsumerProfile consumer = consumerService.getProfileById(id);
        if (consumer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(consumer, HttpStatus.OK);
    }

    @PostMapping("/profile")
    public ResponseEntity<ConsumerProfile> addCosnumer(ConsumerProfile consumerProfile) {
        ConsumerProfile saved = consumerService.addCosnumer(consumerProfile);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

}
