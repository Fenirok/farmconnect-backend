package com.farmconnect.farmconnectbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmconnect.farmconnectbackend.model.NegotiationRequest;
import com.farmconnect.farmconnectbackend.model.NegotiationResponse;
import com.farmconnect.farmconnectbackend.service.NegotiationService;

@RestController
@RequestMapping("/negotiation")
@CrossOrigin(origins = "*") // Allow cross-origin requests
public class NegotiationController {

    @Autowired
    private NegotiationService negotiationService;

    /**
     * Start a new negotiation for a product Consumer initiates negotiation with
     * initial offer
     */
    @PostMapping("/start")
    public ResponseEntity<NegotiationResponse> startNegotiation(@RequestBody NegotiationRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Request body cannot be null"));
        }

        // Validate required fields
        if (request.getProductId() == null || request.getProductId() <= 0) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Valid productId is required"));
        }

        if (request.getFarmerId() == null || request.getFarmerId() <= 0) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Valid farmerId is required"));
        }

        if (request.getConsumerId() == null || request.getConsumerId() <= 0) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Valid consumerId is required"));
        }

        // if (request.getFarmerPrice() <= 0) {
        //     return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Farmer price must be greater than 0"));
        // }
        // if (request.getCostPrice() <= 0) {
        //     return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Cost price must be greater than 0"));
        // }
        // // Validate business logic: farmer price should be >= cost price
        // if (request.getFarmerPrice() < request.getCostPrice()) {
        //     return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Farmer price cannot be less than cost price"));
        // }
        NegotiationResponse response = negotiationService.startNegotiation(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Submit counter offer (Consumer makes initial offer, Farmer makes counter
     * offer) Each party gets only ONE chance to make an offer
     */
    @PostMapping("/counter")
    public ResponseEntity<NegotiationResponse> makeCounterOffer(@RequestBody NegotiationRequest request) {
        if (request == null || request.getNegotiationId() == null) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Invalid request parameters"));
        }

        // Check if at least one valid offer is provided
        boolean hasValidConsumerOffer = request.getConsumerOffer() > 0;
        boolean hasValidFarmerOffer = request.getFarmerOffer() > 0;

        if (!hasValidConsumerOffer && !hasValidFarmerOffer) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Either consumerOffer or farmerOffer must be provided and greater than 0"));
        }

        // Ensure only one type of offer is provided (not both)
        if (hasValidConsumerOffer && hasValidFarmerOffer) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Cannot provide both consumerOffer and farmerOffer in the same request"));
        }

        NegotiationResponse response = negotiationService.makeCounterOffer(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Accept current offer and finalize negotiation Farmer accepts consumer's
     * offer OR Consumer accepts farmer's counter offer
     */
    @PostMapping("/accept")
    public ResponseEntity<NegotiationResponse> acceptOffer(@RequestBody NegotiationRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Request body cannot be null"));
        }

        if (request.getNegotiationId() == null || request.getNegotiationId() <= 0) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Valid negotiationId is required"));
        }

        NegotiationResponse response = negotiationService.acceptOffer(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject current offer and end negotiation Either party can reject to
     * terminate the negotiation
     */
    @PostMapping("/reject")
    public ResponseEntity<NegotiationResponse> rejectOffer(@RequestBody NegotiationRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Request body cannot be null"));
        }

        if (request.getNegotiationId() == null || request.getNegotiationId() <= 0) {
            return ResponseEntity.badRequest().body(new NegotiationResponse(null, 0, 0, 0, "ERROR", "Valid negotiationId is required"));
        }

        NegotiationResponse response = negotiationService.rejectOffer(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all negotiations for a specific consumer
     */
    @GetMapping("/consumer/{consumerId}")
    public ResponseEntity<List<NegotiationResponse>> getNegotiationsForConsumer(@PathVariable Long consumerId) {
        if (consumerId == null || consumerId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<NegotiationResponse> negotiations = negotiationService.getNegotiationsForConsumer(consumerId);
        return ResponseEntity.ok(negotiations);
    }

    /**
     * Get all negotiations for a specific farmer
     */
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<NegotiationResponse>> getNegotiationsForFarmer(@PathVariable Long farmerId) {
        if (farmerId == null || farmerId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<NegotiationResponse> negotiations = negotiationService.getNegotiationsForFarmer(farmerId);
        return ResponseEntity.ok(negotiations);
    }
}
