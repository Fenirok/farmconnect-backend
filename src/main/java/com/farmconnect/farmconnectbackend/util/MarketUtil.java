package com.farmconnect.farmconnectbackend.util;

import java.util.HashMap;
import java.util.Map;

public class MarketUtil {

    // Simple DTO for product info
    public static class ProductMarketInfo {

        private final Long productId;
        private final String productName;
        private final double marketRate;

        public ProductMarketInfo(Long productId, String productName, double marketRate) {
            this.productId = productId;
            this.productName = productName;
            this.marketRate = marketRate;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public double getMarketRate() {
            return marketRate;
        }
    }

    private static final Map<Long, ProductMarketInfo> marketRates = new HashMap<>();

    static {
        marketRates.put(1L, new ProductMarketInfo(2L, "rice", 2500.0));
        marketRates.put(2L, new ProductMarketInfo(1L, "Organic Spinach", 10.0));
        marketRates.put(3L, new ProductMarketInfo(3L, "corn", 1800.0));
        marketRates.put(4L, new ProductMarketInfo(4L, "soybean", 3000.0));
        // Add more products as needed
    }

    public static ProductMarketInfo getMarketInfo(Long productId) {
        return marketRates.get(productId);
    }
}
