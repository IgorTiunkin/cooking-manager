package com.phantom.inventory.services;

import com.phantom.inventory.exceptions.ProductDeleteException;
import com.phantom.inventory.exceptions.ProductSaveException;
import com.phantom.inventory.exceptions.ProductUpdateException;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
        //If product with such name absent - save
        Optional<Product> productByName = getByName(product.getProductName());
        if (productByName.isEmpty()) return productRepository.save(product);

        //if present - if absolute copy - ignore, else - block save if exception
        Product productFromDbByName = productByName.get();
        if (product.equals(productFromDbByName)) return productFromDbByName;
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

    @Transactional
    public Product delete(Integer productId) {
        Optional<Product> productByID = productRepository.findById(productId);
        if (productByID.isEmpty()) throw new ProductDeleteException("Product not found");
        productRepository.delete(productByID.get());
        return productByID.get();
    }

}
