package com.farmconnect.farmconnectbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.farmconnectbackend.model.Negotiation;
import com.farmconnect.farmconnectbackend.model.NegotiationRequest;
import com.farmconnect.farmconnectbackend.model.NegotiationResponse;
import com.farmconnect.farmconnectbackend.model.NegotiationStatus;
import com.farmconnect.farmconnectbackend.strategy.NegotiationStrategy;

@Service
public class NegotiationService {

    private final List<Negotiation> negotiations = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    private NegotiationStrategy negotiationStrategy;

    /**
     * Start a new negotiation
     */
    public NegotiationResponse startNegotiation(NegotiationRequest request) {
        NegotiationResponse response = negotiationStrategy.startNegotiation(request);

        if (!response.getStatus().equals("REJECTED")) {
            Negotiation negotiation = new Negotiation();
            negotiation.setNegotiationId(idGenerator.getAndIncrement());
            negotiation.setProductId(request.getProductId());
            negotiation.setFarmerId(request.getFarmerId());
            negotiation.setConsumerId(request.getConsumerId());
            negotiation.setFarmerPrice(request.getFarmerPrice());

            // MarketUtil.ProductMarketInfo productInfo = MarketUtil.getMarketInfo(request.getProductId());
            // negotiation.setCostPrice(productInfo.getMarketRate());
            // negotiation.setMinPrice(request.getCostPrice() + (request.getCostPrice() * 0.15));
            // Product product = productService.getProductById(request.getProductId());
            // if (product == null) {
            //     throw new IllegalArgumentException("Product not found with ID: " + request.getProductId());
            // }
            // negotiation.setMaxPrice(product.getPrice());
            negotiation.setMinPrice(response.getMinPrice());
            negotiation.setMaxPrice(response.getMaxPrice());
            negotiation.setStatus(NegotiationStatus.ONGOING);

            negotiations.add(negotiation);

            response.setNegotiationId(negotiation.getNegotiationId());
            // response.setMinPrice(negotiation.getMinPrice());
            // response.setMaxPrice(negotiation.getMaxPrice());
            // response.setStatus(negotiation.getStatus().name());

        }

        return response;
    }

    /**
     * Make a counter offer (consumer or farmer)
     */
    public NegotiationResponse makeCounterOffer(NegotiationRequest request) {
        Negotiation negotiation = findNegotiationById(request.getNegotiationId());
        if (negotiation == null) {
            return new NegotiationResponse(null, 0, 0, 0, "ERROR", "Negotiation not found");
        }

        // For consumer counter offer (only allowed once at the beginning)
        if (request.getConsumerOffer() > 0) {
            if (negotiation.getStatus() != NegotiationStatus.ONGOING) {
                return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                        "Consumer can only make initial offer when negotiation is ONGOING");
            }
            return processConsumerCounterOffer(negotiation, request);
        }

        // For farmer counter offer (only allowed once after consumer offer)
        if (request.getFarmerOffer() > 0) {
            if (negotiation.getStatus() != NegotiationStatus.WAITING_FOR_FARMER) {
                return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                        "Farmer can only make counter offer when waiting for farmer response");
            }
            return processFarmerCounterOffer(negotiation, request);
        }

        return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                "No valid offer provided");
    }

    /**
     * Process consumer counter offer
     */
    private NegotiationResponse processConsumerCounterOffer(Negotiation negotiation, NegotiationRequest request) {
        // double minPrice = negotiation.getCostPrice() + (negotiation.getCostPrice() * 0.15);
        double minPrice = negotiation.getMinPrice();
        // double maxPrice = negotiation.getFarmerPrice();
        double maxPrice = negotiation.getMaxPrice();

        // Validate consumer offer
        if (request.getConsumerOffer() < minPrice) {
            return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                    "Offer too low. Minimum acceptable price is " + minPrice);
        }

        if (request.getConsumerOffer() > maxPrice) {
            return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                    "Offer too high. Maximum price is " + maxPrice);
        }

        // Update negotiation with consumer offer
        negotiation.setConsumerOffer(request.getConsumerOffer());
        negotiation.setStatus(NegotiationStatus.WAITING_FOR_FARMER);
        
        return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                "Consumer offer received. Farmer can now Accept, Reject, or Counter.");
    }

    /**
     * Process farmer counter offer
     */
    private NegotiationResponse processFarmerCounterOffer(Negotiation negotiation, NegotiationRequest request) {
        double minPrice = negotiation.getConsumerOffer(); // Farmer must offer at least consumer's offer
        double maxPrice = negotiation.getMaxPrice();

        // Validate farmer offer (should be between consumer offer and farmer's max)
        if (request.getFarmerOffer() < minPrice) {
            return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                    "Farmer offer too low. Must be at least " + minPrice);
        }

        if (request.getFarmerOffer() > maxPrice) {
            return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                    "Farmer offer too high. Maximum price is " + maxPrice);
        }

        // Update negotiation with farmer offer
        negotiation.setFarmerOffer(request.getFarmerOffer());
        negotiation.setStatus(NegotiationStatus.WAITING_FOR_CONSUMER);

        return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                "Farmer counter offer received. Consumer can now Accept or Reject.");
    }

    /**
     * Accept current offer (consumer accepts farmer's price or farmer accepts
     * consumer's offer)
     */
    public NegotiationResponse acceptOffer(NegotiationRequest request) {
        Negotiation negotiation = findNegotiationById(request.getNegotiationId());
        if (negotiation == null) {
            return new NegotiationResponse(null, 0, 0, 0, "ERROR", "Negotiation not found");
        }

        // Determine who is accepting and what price to use
        double agreedPrice;
        String message;

        if (negotiation.getStatus() == NegotiationStatus.WAITING_FOR_FARMER) {
            // Farmer is accepting consumer's offer
            if (negotiation.getConsumerOffer() == null) {
                return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                        "No consumer offer to accept");
            }
            agreedPrice = negotiation.getConsumerOffer();
            message = "Farmer accepted consumer offer of " + agreedPrice;
        } else if (negotiation.getStatus() == NegotiationStatus.WAITING_FOR_CONSUMER) {
            // Consumer is accepting farmer's counter offer
            if (negotiation.getFarmerOffer() == null) {
                return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                        "No farmer offer to accept");
            }
            agreedPrice = negotiation.getFarmerOffer();
            message = "Consumer accepted farmer offer of " + agreedPrice;
        } else {
            return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                    "Cannot accept offer in current status: " + negotiation.getStatus());
        }

        // Finalize the negotiation
        negotiation.setAgreedPrice(agreedPrice);
        negotiation.setStatus(NegotiationStatus.FINALIZED);

        return new NegotiationResponse(
                negotiation.getNegotiationId(),
                negotiation.getCostPrice() + (negotiation.getCostPrice() * 0.15),
                negotiation.getFarmerPrice(),
                agreedPrice,
                "FINALIZED",
                message
        );
    }

    /**
     * Reject current offer and end negotiation
     */
    public NegotiationResponse rejectOffer(NegotiationRequest request) {
        Negotiation negotiation = findNegotiationById(request.getNegotiationId());
        if (negotiation == null) {
            return new NegotiationResponse(null, 0, 0, 0, "ERROR", "Negotiation not found");
        }

        String message;
        if (negotiation.getStatus() == NegotiationStatus.WAITING_FOR_FARMER) {
            // Farmer rejected consumer's offer
            message = "Farmer rejected consumer offer of " + negotiation.getConsumerOffer();
        } else if (negotiation.getStatus() == NegotiationStatus.WAITING_FOR_CONSUMER) {
            // Consumer rejected farmer's counter offer
            message = "Consumer rejected farmer offer of " + negotiation.getFarmerOffer();
        } else {
            return new NegotiationResponse(negotiation.getNegotiationId(), 0, 0, 0, "ERROR",
                    "Cannot reject offer in current status: " + negotiation.getStatus());
        }

        // End the negotiation
        negotiation.setStatus(NegotiationStatus.REJECTED);

        return new NegotiationResponse(
                negotiation.getNegotiationId(),
                negotiation.getCostPrice() + (negotiation.getCostPrice() * 0.15),
                negotiation.getFarmerPrice(),
                0,
                "REJECTED",
                message
        );
    }

    /**
     * Get all negotiations for a specific consumer
     */
    public List<NegotiationResponse> getNegotiationsForConsumer(Long consumerId) {
        return negotiations.stream()
                .filter(n -> n.getConsumerId().equals(consumerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all negotiations for a specific farmer
     */
    public List<NegotiationResponse> getNegotiationsForFarmer(Long farmerId) {
        return negotiations.stream()
                .filter(n -> n.getFarmerId().equals(farmerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private Negotiation findNegotiationById(Long negotiationId) {
        return negotiations.stream()
                .filter(n -> n.getNegotiationId().equals(negotiationId))
                .findFirst()
                .orElse(null);
    }

    private NegotiationResponse convertToResponse(Negotiation negotiation) {
        double minPrice = negotiation.getCostPrice() + (negotiation.getCostPrice() * 0.15);
        return new NegotiationResponse(
                negotiation.getNegotiationId(),
                minPrice,
                negotiation.getFarmerPrice(),
                negotiation.getConsumerOffer() != null ? negotiation.getConsumerOffer() : 0,
                negotiation.getStatus().toString(),
                "Negotiation status: " + negotiation.getStatus()
        );
    }
}
