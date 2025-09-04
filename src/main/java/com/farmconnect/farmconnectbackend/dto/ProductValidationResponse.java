package com.farmconnect.farmconnectbackend.dto;

import lombok.Data;
import java.util.List;
import com.farmconnect.farmconnectbackend.model.Product;

@Data
public class ProductValidationResponse {
    private boolean success;
    private String message;
    private Product product;
    private AIValidationDetails aiValidation;
    
    @Data
    public static class AIValidationDetails {
        private boolean accepted;
        private String reason;
        private List<YOLOResult> yoloResults;
    }
    
    @Data
    public static class YOLOResult {
        private String label;
        private double confidence;
    }
}
