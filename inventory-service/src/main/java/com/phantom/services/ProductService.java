package com.phantom.services;

import com.phantom.models.Product;
import com.phantom.repositories.ProductRepository;
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
