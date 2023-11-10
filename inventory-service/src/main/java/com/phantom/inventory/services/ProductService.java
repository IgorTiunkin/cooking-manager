package com.phantom.inventory.services;

import com.phantom.inventory.exceptions.ProductSaveException;
import com.phantom.inventory.exceptions.ProductUpdateException;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional <Product> getById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Transactional
    public Product save(Product product) {
        Optional<Product> productByName = getByName(product.getProductName());
        if (productByName.isEmpty()) return productRepository.save(product);
        Product productFromDbByName = productByName.get();
        if (product.getProductId().equals(productFromDbByName.getProductId())) return productRepository.save(product);
        throw new ProductSaveException("Such product name already present");
    }

    @Transactional
    public Product update(Product product) {
        Optional<Product> productByName = getByName(product.getProductName());
        if (productByName.isEmpty()) return productRepository.save(product);
        Product productFromDbByName = productByName.get();
        if (product.getProductId().equals(productFromDbByName.getProductId())) return productRepository.save(product);
        throw new ProductUpdateException("Such product name already present");
    }

    public Optional <Product> getByName(String name) {
        return productRepository.findByProductName(name);
    }


}
