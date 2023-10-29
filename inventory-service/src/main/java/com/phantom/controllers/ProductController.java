package com.phantom.controllers;

import com.phantom.dto.ProductDTO;
import com.phantom.models.Product;
import com.phantom.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;

    @GetMapping ("/decription")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getProductsDescription(@RequestBody List <Integer> listOfProductId) {
        List<Product> products = productService.getAllById(listOfProductId);
        return products.stream().map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    @GetMapping ("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts() {
        List<Product> allProducts = productService.getAllProducts();
        return allProducts.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    private ProductDTO convertToProductDTO (Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

}
