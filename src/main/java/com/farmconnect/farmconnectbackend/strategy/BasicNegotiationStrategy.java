package com.farmconnect.farmconnectbackend.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.farmconnect.farmconnectbackend.model.Negotiation;
import com.farmconnect.farmconnectbackend.model.NegotiationRequest;
import com.farmconnect.farmconnectbackend.model.NegotiationResponse;
import com.farmconnect.farmconnectbackend.model.Product;
import com.farmconnect.farmconnectbackend.service.ProductService;
import com.farmconnect.farmconnectbackend.util.MarketUtil;

@Component
public class BasicNegotiationStrategy implements NegotiationStrategy {

    private final ProductService productService;

    @Autowired
    public BasicNegotiationStrategy(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public NegotiationResponse startNegotiation(NegotiationRequest request) {
        // Fetch product
        Product product = productService.getProductById(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + request.getProductId());
        }

        // Fetch market price for the product
        MarketUtil.ProductMarketInfo productInfo = MarketUtil.getMarketInfo(request.getProductId());
        double marketPrice = productInfo.getMarketRate();

        // Validate farmer price (should not exceed 10% above market rate)
        /* if (request.getFarmerPrice() > marketPrice * 1.10) {
            return new NegotiationResponse(null, 0, 0, 0, "REJECTED",
                    "Farmer price exceeds 10% above market rate");
        } */
        // Calculate min negotiable price = cost price + 15%
        double costPrice = productInfo.getMarketRate();
        double minPrice = costPrice * 1.15;

        // double maxPrice = request.getFarmerPrice();
        double maxPrice = product.getPrice();

        System.out.println(minPrice);
        System.out.println(maxPrice);

        if (minPrice > maxPrice) {
            return new NegotiationResponse(null, 0, 0, 0, "REJECTED",
                    "Invalid pricing: min price is greater than farmer price");
        }

        return new NegotiationResponse(null, minPrice, maxPrice, 0, "ONGOING",
                "Negotiation started successfully");
    }

    @Override
    public NegotiationResponse processCounterOffer(Negotiation negotiation, NegotiationRequest request) {
        double minPrice = negotiation.getCostPrice() + (negotiation.getCostPrice() * 0.15);
        double maxPrice = negotiation.getFarmerPrice();
        double offer = request.getConsumerOffer();

        if (offer < minPrice) {
            return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, 0, "ONGOING",
                    "Offer too low. Minimum acceptable price is " + minPrice);
        }

        // For now, simple midpoint logic for agreed price
        double agreedPrice = (offer + maxPrice) / 2;

        return new NegotiationResponse(negotiation.getNegotiationId(), minPrice, maxPrice, agreedPrice,
                "ONGOING", "Counter offer accepted, suggested price: " + agreedPrice);
    }
}
