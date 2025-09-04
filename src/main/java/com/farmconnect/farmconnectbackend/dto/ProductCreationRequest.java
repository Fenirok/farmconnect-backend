package com.farmconnect.farmconnectbackend.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ProductCreationRequest {
    private String name;
    private String description;
    private double price;
    private String category;
    private String farmName;
    private double weight;
    private String unit;
    private boolean isOrganic;
    private String location;
    private MultipartFile image;
}
