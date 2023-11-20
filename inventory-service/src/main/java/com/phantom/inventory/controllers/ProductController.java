package com.phantom.inventory.controllers;

import com.phantom.inventory.dto.ProductDTO;
import com.phantom.inventory.exceptions.ProductDeleteException;
import com.phantom.inventory.exceptions.ProductNotFoundException;
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

    @GetMapping ("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts() {
        log.info("Requested all products");
        return productService.getAllProducts();
    }

    @GetMapping ("/in")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getProductsIn(@RequestParam ("productIds") Set <Integer> productIdList) {
        log.info("Requested products in list");
        return productService.getAllById(new ArrayList<>(productIdList));
    }

    @GetMapping ("/one")
    public ResponseEntity <ProductDTO> getProductById(@RequestParam ("productId") Integer productId) {
        log.info("Request product. Id: {}", productId);
        ProductDTO productDTO = productService.getById(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductNotFoundException.class)
    public ResponseEntity <ProductDTO> failedSaveProduct(ProductNotFoundException productNotFoundException) {
        log.info("Product not found.");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/save")
    public ResponseEntity <ProductDTO> saveProduct(@RequestBody ProductDTO productDTO) {
        log.info("Request save product. Name {}", productDTO.getProductName());
        ProductDTO savedProductDTO = productService.save(productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductSaveException.class)
    public ResponseEntity <ProductDTO> failedSaveProduct(ProductSaveException productSaveException) {
        log.info("Save failed. {}", productSaveException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @PostMapping ("/update")
    public ResponseEntity <ProductDTO> updateProduct (@RequestBody ProductDTO productDTO) {
        log.info("Request edit product. Id: {}", productDTO.getProductId());
        ProductDTO updatedProductDTO = productService.update(productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductUpdateException.class)
    public ResponseEntity <ProductDTO> failedUpdateProduct(ProductUpdateException productUpdateException) {
        log.info("Update failed. {}", productUpdateException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ProductDTO> deleteProduct (@RequestParam ("productId") Integer productId) {
        log.info("Request delete product. Id {}", productId);
        ProductDTO deletedProductDTO = productService.delete(productId);
        log.info("Product successfully deleted. Id {}", deletedProductDTO.getProductId());
        return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);
    }

    @ExceptionHandler (ProductDeleteException.class)
    public ResponseEntity<ProductDTO> failedDeleteProduct(ProductDeleteException productDeleteException){
        log.info("Delete Failed {}", productDeleteException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
