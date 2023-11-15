package com.phantom.inventory.controllers;

import com.phantom.inventory.dto.ProductDTO;
import com.phantom.inventory.exceptions.ProductDeleteException;
import com.phantom.inventory.exceptions.ProductSaveException;
import com.phantom.inventory.exceptions.ProductUpdateException;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;

    @GetMapping ("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts() {
        log.info("Requested all products");
        List<Product> allProducts = productService.getAllProducts();
        return allProducts.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    @GetMapping ("/in")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getProductsIn(@RequestParam ("recipeIds") Set <Integer> productIdList) {
        log.info("Requested products in list");
        List<Product> productList = productService.getAllById(new ArrayList<>(productIdList));
        return productList.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    @GetMapping ("/one")
    public ResponseEntity <ProductDTO> getProductById(@RequestParam ("productId") Integer productId) {
        log.info("Request product. Id: {}", productId);
        Optional<Product> productById = productService.getById(productId);
        if (productById.isEmpty()) {
            log.info("Product not found. Id = {}", productId);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Product product = productById.get();
        ProductDTO productDTO = convertToProductDTO(product);
        log.info("Product found. Id = {}", productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity <ProductDTO> saveProduct(@RequestBody ProductDTO productDTO) {
        //Convert to product and save
        log.info("Request save product. Name {}", productDTO.getProductName());
        Product product = convertToProduct(productDTO);
        Product savedProduct = productService.save(product);

        //Convert to DTO and return
        ProductDTO savedProductDTO = convertToProductDTO(savedProduct);
        log.info("Save product. Id: {}", savedProductDTO.getProductId());
        return new ResponseEntity<>(savedProductDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductSaveException.class)
    public ResponseEntity <ProductDTO> failedSaveProduct(ProductSaveException productSaveException) {
        log.info("Save failed. {}", productSaveException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ProductDTO> deleteProduct (@RequestParam ("productId") Integer productId) {
        log.info("Request delete product. Id {}", productId);
        Product deletedProduct = productService.delete(productId);
        ProductDTO deletedProductDTO = convertToProductDTO(deletedProduct);
        log.info("Product successfully deleted. Id {}", deletedProductDTO.getProductId());
        return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductDeleteException.class)
    public ResponseEntity<ProductDTO> failedDeleteProduct(ProductDeleteException productDeleteException){
        log.info("Delete Failed {}", productDeleteException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping ("/update")
    public ResponseEntity <ProductDTO> updateProduct (@RequestBody ProductDTO productDTO) {
        log.info("Request edit product. Id: {}", productDTO.getProductId());
        Product product = convertToProduct(productDTO);
        Product updatedProduct = productService.update(product);
        ProductDTO updatedProductDTO = convertToProductDTO(updatedProduct);
        log.info("Edit product. Id: {}", updatedProduct.getProductId());
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductUpdateException.class)
    public ResponseEntity <ProductDTO> failedUpdateProduct(ProductUpdateException productUpdateException) {
        log.info("Update failed. {}", productUpdateException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    private ProductDTO convertToProductDTO (Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    private Product convertToProduct (ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
}
