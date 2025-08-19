package com.farmconnect.farmconnectbackend.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmconnect.farmconnectbackend.model.Product;
import com.farmconnect.farmconnectbackend.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
