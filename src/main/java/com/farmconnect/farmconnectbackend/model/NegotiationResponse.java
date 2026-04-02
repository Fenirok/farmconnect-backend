package com.farmconnect.farmconnectbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationResponse {

    private Long negotiationId;
    private double minPrice;
    private double maxPrice;
    private double agreedPrice;
    private String status;
    private String message;
}
