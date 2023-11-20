package com.phantom.inventory.services;

import com.phantom.inventory.dto.ProductDTO;
import com.phantom.inventory.exceptions.ProductDeleteException;
import com.phantom.inventory.exceptions.ProductNotFoundException;
import com.phantom.inventory.exceptions.ProductSaveException;
import com.phantom.inventory.exceptions.ProductUpdateException;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<ProductDTO> getAllById(List <Integer> listOfProductId) {
        List<Product> productList= productRepository.findAllByProductIdIn(listOfProductId);
        return productList.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    public ProductDTO getById(Integer productId) {
        log.info("Request product. Id: {}", productId);
        Optional<Product> productById = productRepository.findById(productId);
        if (productById.isEmpty()) {
            log.info("Product not found. Id = {}", productId);
            throw new ProductNotFoundException("product not found");
        }
        Product product = productById.get();
        ProductDTO productDTO = convertToProductDTO(product);
        log.info("Product found. Id = {}", productId);
        return productDTO;
    }

    public Optional <Product> getByName(String name) {
        return productRepository.findByProductName(name);
    }

    @Transactional
    public ProductDTO save(ProductDTO productDTO) {

        Product product = convertToProduct(productDTO);

        //If product with such name absent - save
        Optional<Product> productByName = getByName(product.getProductName());
        if (productByName.isEmpty()) {
            log.info("Save product. Id: {}", product.getProductId());
            Product savedProduct = productRepository.save(product);
            return convertToProductDTO(savedProduct);
        }

        //if present - if absolute copy - ignore, else - block save if exception
        Product productFromDbByName = productByName.get();
        if (product.equals(productFromDbByName)) return convertToProductDTO(productFromDbByName);
        throw new ProductSaveException("Such product name already present");
    }

    @Transactional
    public ProductDTO update(ProductDTO productDTO) {

        Product product = convertToProduct(productDTO);

        //If product with such name absent - save
        Optional<Product> productByName = getByName(product.getProductName());
        if (productByName.isEmpty()) {
            log.info("Edit product. Id: {}", product.getProductId());
            Product updatedProduct = productRepository.save(product);
            return convertToProductDTO(updatedProduct);
        }

        //if present - if absolute copy - ignore, else - block save if exception
        Product productFromDbByName = productByName.get();
        if (product.equals(productFromDbByName)) return convertToProductDTO(productFromDbByName);
        throw new ProductUpdateException("Such product name already present");
    }

    @Transactional
    public ProductDTO delete(Integer productId) {
        Optional<Product> productByID = productRepository.findById(productId);
        if (productByID.isEmpty()) throw new ProductDeleteException("Product not found");

        Product product = productByID.get();
        productRepository.delete(product);
        return convertToProductDTO(product);
    }

    private ProductDTO convertToProductDTO (Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    private Product convertToProduct (ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

}
