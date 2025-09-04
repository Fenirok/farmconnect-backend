package com.farmconnect.farmconnectbackend.controller;

import java.util.List;
import java.util.Map;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.dao.EmptyResultDataAccessException;
import com.cloudinary.Cloudinary;
import com.farmconnect.farmconnectbackend.model.Product;
import com.farmconnect.farmconnectbackend.service.ProductService;
import com.farmconnect.farmconnectbackend.dto.ProductValidationResponse;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable long id) {
        Product prod = productService.getProductById(id);
        if (prod == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(prod, HttpStatus.OK);
    }

    /*
    @GetMapping("/farmers/{farmerId}/products")
    public ResponseEntity<List<Product>> getProductsByFarmer(@PathVariable Long farmerId) {
        List<Product> products = productService.getProductsByFarmerId(farmerId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    */
    
    @PostMapping("/farmers/{farmerId}/products")
    public ResponseEntity<Product> addProduct(
        @PathVariable Long farmerId,
        @RequestBody Product product
    ) {
        try {
            product.setFarmerId(farmerId);
            Product saved = productService.addProduct(product);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/farmers/{farmerId}/products/validate")
    public ResponseEntity<ProductValidationResponse> addProductWithImageValidation(
        @PathVariable Long farmerId,
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("price") double price,
        @RequestParam("category") String category,
        @RequestParam("farmName") String farmName,
        @RequestParam("weight") double weight,
        @RequestParam("unit") String unit,
        @RequestParam("isOrganic") boolean isOrganic,
        @RequestParam("location") String location,
        @RequestParam("image") MultipartFile image
    ) {
        try {
            // Create product object
            Product product = new Product();
            product.setFarmerId(farmerId);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.setFarmName(farmName);
            product.setWeight(weight);
            product.setUnit(unit);
            product.setOrganic(isOrganic);
            product.setLocation(location);
            
            // Process with AI validation
            ProductValidationResponse response = productService.addProductWithImageValidation(product, image);
            
            if (response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            ProductValidationResponse errorResponse = new ProductValidationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error processing request: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/ai-service/health")
    public ResponseEntity<String> checkAIServiceHealth() {
        try {
            // Make actual health check to AI microservice
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:8000/health", 
                String.class
            );
            return new ResponseEntity<>("AI Service is healthy: " + response.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("AI Service not available: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    @PatchMapping("/products/update/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        Product updated = productService.updateProduct(productId, product);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/products/delete/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
