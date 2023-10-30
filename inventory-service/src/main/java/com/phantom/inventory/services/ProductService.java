package com.phantom.inventory.services;

import com.phantom.inventory.models.Product;
import com.phantom.inventory.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllById(List <Integer> listOfProductId) {
        return productRepository.findAllByProductIdIn(listOfProductId);
    }

    public List <Product> getAllProducts() {
        return productRepository.findAll();
    }
}
