package com.phantom.inventory.controllers;

import com.phantom.inventory.dto.ProductInStockDTO;
import com.phantom.inventory.dto.ProductsForPrepareDTO;
import com.phantom.inventory.dto.StockUpdateDTO;
import com.phantom.inventory.exceptions.ProductNotEnoughQuantityException;
import com.phantom.inventory.exceptions.ProductNotFoundException;
import com.phantom.inventory.exceptions.ProductStockAlreadyChanged;
import com.phantom.inventory.mappers.ProductInStockToPrepareDTOMapper;
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
    private final ProductInStockToPrepareDTOMapper productInStockToPrepareDTOMapper;

    @GetMapping("/get-by-id")
    public ResponseEntity <Integer> getProductInStockById(@RequestParam ("productId") Integer productId) {
        log.info("Request stock for id {}", productId);
        Integer quantityById = productInStockService.getQuantityById(productId);
        log.info("Found {} in stock", quantityById);
        return new ResponseEntity<>(quantityById, HttpStatus.OK);
    }

    @PostMapping("/change")
    public ResponseEntity<ProductInStockDTO> changeStock(@RequestBody StockUpdateDTO stockUpdateDTO) {
        log.info("Request Stock update, id {}, quantity {}", stockUpdateDTO.getProductId(), stockUpdateDTO.getChange());
        ProductInStock productInStock = productInStockService.updateStock(stockUpdateDTO);
        log.info("Stock changed. new quantity {}", productInStock.getQuantity());
        ProductInStockDTO productInStockDTO = convertToProductInStockDTO(productInStock);
        return new ResponseEntity<>(productInStockDTO, HttpStatus.OK);
    }

    @ExceptionHandler({ProductNotFoundException.class, ProductNotEnoughQuantityException.class})
    public ResponseEntity<ProductInStockDTO> failedChangeStock(RuntimeException runtimeException) {
        log.info("Change stock exception {}", runtimeException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductStockAlreadyChanged.class)
    public ResponseEntity<ProductInStockDTO> duplicateChangeStock(ProductStockAlreadyChanged productStockAlreadyChanged) {
        log.info("Change stock exception {}", productStockAlreadyChanged.getMessage());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/stocks")
    public ResponseEntity<List <ProductsForPrepareDTO>> getStocksForProductIds(
            @RequestParam ("productIds") List <Integer> productIds) {
        log.info("Requested product in stock by ids {}", productIds);
        List<ProductInStock> productInStockByIds = productInStockService.getProductInStockByIds(productIds);
        List<ProductsForPrepareDTO> productsForPrepareDTOS = productInStockByIds.stream().map(productInStockToPrepareDTOMapper::convertToPrepareDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productsForPrepareDTOS, HttpStatus.OK);
    }

    private ProductInStockDTO convertToProductInStockDTO(ProductInStock productInStock) {
        return modelMapper.map(productInStock, ProductInStockDTO.class);
    }
}
