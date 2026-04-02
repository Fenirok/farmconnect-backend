package com.farmconnect.farmconnectbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationRequest {

    private Long negotiationId;    // For counter or finalize
    private Long productId;
    private Long farmerId;
    private Long consumerId;
    private double farmerPrice;
    private double consumerOffer;
    private double farmerOffer;    // For farmer counter offers
    private double costPrice;
    private String action; // "START", "COUNTER", "FINALIZE" (optional, for clarity)
}
