package com.phantom.inventory.controllers;

import com.phantom.inventory.dto.ProductInStockDTO;
import com.phantom.inventory.models.ProductInStock;
import com.phantom.inventory.services.ProductInStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product-in-stock")
@RequiredArgsConstructor
@Slf4j
public class ProductInStockController {

    private final ProductInStockService productInStockService;
    private final ModelMapper modelMapper;

    @GetMapping("/get-by-id")
    public ResponseEntity <Integer> getProductInStockById(@RequestParam ("productId") Integer productId) {
        log.info("Request stock for id {}", productId);
        Integer quantityById = productInStockService.getQuantityById(productId);
        log.info("Found {} in stock", quantityById);
        return new ResponseEntity<>(quantityById, HttpStatus.OK);
    }


    @GetMapping("/check-stock")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductInStockDTO> getAllStockForIds (@RequestBody List <Integer> listOfProductId) {
        List<ProductInStock> allStockQuantityById = productInStockService.getAllStockQuantityById(listOfProductId);
        return allStockQuantityById.stream().map(this::convertToProductInStockDTO)
                .collect(Collectors.toList());
    }

    private ProductInStockDTO convertToProductInStockDTO(ProductInStock productInStock) {
        return modelMapper.map(productInStock, ProductInStockDTO.class);
    }
}
