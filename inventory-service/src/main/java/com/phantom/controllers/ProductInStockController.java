package com.phantom.controllers;

import com.phantom.dto.ProductDTO;
import com.phantom.dto.ProductInStockDTO;
import com.phantom.models.ProductInStock;
import com.phantom.services.ProductInStockService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product-in-stock")
@RequiredArgsConstructor
public class ProductInStockController {

    private final ProductInStockService productInStockService;
    private final ModelMapper modelMapper;

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
