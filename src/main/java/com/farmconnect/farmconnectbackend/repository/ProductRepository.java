package com.farmconnect.farmconnectbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmconnect.farmconnectbackend.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFarmerId(Long farmerId);
}
