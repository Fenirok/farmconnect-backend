package com.farmconnect.farmconnectbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmconnect.farmconnectbackend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
