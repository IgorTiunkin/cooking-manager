package com.phantom.inventory.controllers;

import com.phantom.inventory.dto.ProductDTO;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
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
        log.info("Requested all products");
        List<Product> allProducts = productService.getAllProducts();
        return allProducts.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }

    private ProductDTO convertToProductDTO (Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    @GetMapping ("/in")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getProductsIn(@RequestParam ("recipeIds") Set <Integer> productIdList) {
        log.info("Requested products in list");
        List<Product> productList = productService.getAllById(new ArrayList<>(productIdList));
        return productList.stream().map(this::convertToProductDTO).collect(Collectors.toList());
    }
}
