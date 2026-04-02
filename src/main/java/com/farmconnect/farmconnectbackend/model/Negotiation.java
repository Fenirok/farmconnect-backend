package com.farmconnect.farmconnectbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Negotiation {

    private Long negotiationId;
    private Long productId;
    private Long farmerId;
    private Long consumerId;

    private double farmerPrice;     // Price set by farmer
    private double costPrice;       // Verified cost price
    private double minPrice;        // costPrice + 15%
    private double maxPrice;        // farmer's listed price
    private Double consumerOffer;   // last offer by consumer
    private Double farmerOffer;     // last offer by farmer
    private Double agreedPrice;     // final agreed price (only after accept)

    private NegotiationStatus status; // ONGOING, WAITING_FOR_FARMER, WAITING_FOR_CONSUMER, FINALIZED
}
