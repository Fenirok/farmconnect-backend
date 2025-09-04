package com.farmconnect.farmconnectbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIMicroserviceService {

    @Value("${ai.microservice.url:http://localhost:8000}")
    private String aiMicroserviceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AIMicroserviceService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public AIValidationResponse validateProductImage(String productName, MultipartFile imageFile) {
        try {
            // Convert image to base64
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            
            // Prepare request payload
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("product_name", productName);
            requestPayload.put("image", base64Image);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
            
            // Make request to AI microservice
            ResponseEntity<String> response = restTemplate.exchange(
                aiMicroserviceUrl + "/verify",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            // Parse response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            
            AIValidationResponse validationResponse = new AIValidationResponse();
            validationResponse.setAccepted(responseJson.get("accepted").asBoolean());
            validationResponse.setReason(responseJson.get("reason").asText());
            
            // Parse YOLO results if available
            if (responseJson.has("yolo") && responseJson.get("yolo").isArray()) {
                JsonNode yoloArray = responseJson.get("yolo");
                for (JsonNode yoloItem : yoloArray) {
                    YOLOResult yoloResult = new YOLOResult();
                    yoloResult.setLabel(yoloItem.get("label").asText());
                    yoloResult.setConfidence(yoloItem.get("confidence").asDouble());
                    validationResponse.addYoloResult(yoloResult);
                }
            }
            
            return validationResponse;
            
        } catch (Exception e) {
            // Log the error and return a failed validation response
            e.printStackTrace();
            AIValidationResponse errorResponse = new AIValidationResponse();
            errorResponse.setAccepted(false);
            errorResponse.setReason("Error communicating with AI service: " + e.getMessage());
            return errorResponse;
        }
    }

    // Inner classes for response handling
    public static class AIValidationResponse {
        private boolean accepted;
        private String reason;
        private java.util.List<YOLOResult> yoloResults = new java.util.ArrayList<>();

        // Getters and setters
        public boolean isAccepted() { return accepted; }
        public void setAccepted(boolean accepted) { this.accepted = accepted; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public java.util.List<YOLOResult> getYoloResults() { return yoloResults; }
        public void setYoloResults(java.util.List<YOLOResult> yoloResults) { this.yoloResults = yoloResults; }
        
        public void addYoloResult(YOLOResult result) { this.yoloResults.add(result); }
    }

    public static class YOLOResult {
        private String label;
        private double confidence;

        // Getters and setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
}
