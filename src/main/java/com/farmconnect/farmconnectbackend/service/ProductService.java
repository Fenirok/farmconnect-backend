package com.farmconnect.farmconnectbackend.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.farmconnect.farmconnectbackend.model.Product;
import com.farmconnect.farmconnectbackend.repository.ProductRepository;
import com.farmconnect.farmconnectbackend.dto.ProductValidationResponse;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AIMicroserviceService aiMicroserviceService;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElse(null);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
    
    public ProductValidationResponse addProductWithImageValidation(Product product, MultipartFile imageFile) {
        ProductValidationResponse response = new ProductValidationResponse();
        
        try {
            // Validate image with AI microservice
            AIMicroserviceService.AIValidationResponse aiResponse = 
                aiMicroserviceService.validateProductImage(product.getName(), imageFile);
            
            if (aiResponse.isAccepted()) {
                // AI validation passed, save the product
                Product savedProduct = productRepository.save(product);
                
                response.setSuccess(true);
                response.setMessage("Product added successfully");
                response.setProduct(savedProduct);
                
                // Add AI validation details
                ProductValidationResponse.AIValidationDetails aiDetails = new ProductValidationResponse.AIValidationDetails();
                aiDetails.setAccepted(true);
                aiDetails.setReason(aiResponse.getReason());
                
                // Convert YOLO results
                List<ProductValidationResponse.YOLOResult> yoloResults = new java.util.ArrayList<>();
                for (AIMicroserviceService.YOLOResult yolo : aiResponse.getYoloResults()) {
                    ProductValidationResponse.YOLOResult result = new ProductValidationResponse.YOLOResult();
                    result.setLabel(yolo.getLabel());
                    result.setConfidence(yolo.getConfidence());
                    yoloResults.add(result);
                }
                aiDetails.setYoloResults(yoloResults);
                response.setAiValidation(aiDetails);
                
            } else {
                // AI validation failed
                response.setSuccess(false);
                response.setMessage("Image validation failed: " + aiResponse.getReason());
                response.setProduct(null);
                
                // Add AI validation details
                ProductValidationResponse.AIValidationDetails aiDetails = new ProductValidationResponse.AIValidationDetails();
                aiDetails.setAccepted(false);
                aiDetails.setReason(aiResponse.getReason());
                response.setAiValidation(aiDetails);
            }
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error processing product: " + e.getMessage());
            response.setProduct(null);
        }
        
        return response;
    }

    public List<Product> getProductsByFarmerId(Long farmerId) {
        return productRepository.findByFarmerId(farmerId);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public Product updateProduct(Long productId, Product updatedProduct) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return null;
        }
        Product existingProduct = optionalProduct.get();
        // Update only fields that are provided (not null or not default)
        if (updatedProduct.getName() != null) existingProduct.setName(updatedProduct.getName());
        if (updatedProduct.getDescription() != null) existingProduct.setDescription(updatedProduct.getDescription());
        if (updatedProduct.getPrice() != 0) existingProduct.setPrice(updatedProduct.getPrice());
        if (updatedProduct.getImageUrl() != null) existingProduct.setImageUrl(updatedProduct.getImageUrl());
        if (updatedProduct.getCategory() != null) existingProduct.setCategory(updatedProduct.getCategory());
        if (updatedProduct.getFarmName() != null) existingProduct.setFarmName(updatedProduct.getFarmName());
        if (updatedProduct.getWeight() != 0) existingProduct.setWeight(updatedProduct.getWeight());
        if (updatedProduct.getUnit() != null) existingProduct.setUnit(updatedProduct.getUnit());
        existingProduct.setOrganic(updatedProduct.isOrganic());
        if (updatedProduct.getLocation() != null) existingProduct.setLocation(updatedProduct.getLocation());
        return productRepository.save(existingProduct);
    }
}
