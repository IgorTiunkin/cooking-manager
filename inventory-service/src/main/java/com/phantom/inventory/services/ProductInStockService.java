package com.phantom.inventory.services;

import com.phantom.inventory.dto.ProductAndQuantityDTO;
import com.phantom.inventory.dto.RecipeCookingOrderDTO;
import com.phantom.inventory.exceptions.ProductNotEnoughQuantityException;
import com.phantom.inventory.dto.StockUpdateDTO;
import com.phantom.inventory.exceptions.ProductNotFoundException;
import com.phantom.inventory.exceptions.ProductStockAlreadyChanged;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.models.ProductInStock;
import com.phantom.inventory.models.StockChange;
import com.phantom.inventory.repositories.ProductInStockRepository;
import com.phantom.inventory.repositories.ProductRepository;
import com.phantom.inventory.repositories.StockChangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductInStockService {

    private final ProductInStockRepository productInStockRepository;
    private final StockChangeRepository stockChangeRepository;
    private final ProductRepository productRepository;

    public List <ProductInStock> getProductInStockByIds(List<Integer> listOfProductId) {
        return productInStockRepository.findAllByProductIn(listOfProductId);
    }

    public Integer getQuantityById(Integer productId) {
        return productInStockRepository.getQuantityById(productId).orElse(0);
    }

    @Transactional
    public ProductInStock updateStock(StockUpdateDTO stockUpdateDTO) {
        //Check for repeat with timestamp
        LocalDateTime timestamp = stockUpdateDTO.getTimestamp();
        Optional<StockChange> stockChangeRepositoryByTimestamp = stockChangeRepository.findByTimestamp(timestamp);
        if (stockChangeRepositoryByTimestamp.isPresent()) throw new ProductStockAlreadyChanged("already changed");

        //Check if product stillpresent
        Integer productId = stockUpdateDTO.getProductId();
        Optional<Product> productById = productRepository.findById(productId);
        if (productById.isEmpty()) throw new ProductNotFoundException("Product not found");

        //Check if enough to change
        Optional<ProductInStock> productInStockById = productInStockRepository.findById(productId);
        Integer quantityChange = stockUpdateDTO.getChange();
        ProductInStock productInStock = productInStockById.orElse(ProductInStock.builder()
                .product(productById.get())
                .quantity(0)
                .build()
        );
        Integer quantityInStock = productInStock.getQuantity();
        if (quantityChange + quantityInStock < 0) throw new ProductNotEnoughQuantityException("Not enough");

        StockChange stockChange = StockChange.builder()
                .product(productById.get())
                .change(quantityChange)
                .timestamp(timestamp).build();
        stockChangeRepository.save(stockChange);
        productInStock.setQuantity(quantityChange+quantityInStock);
        productInStockRepository.save(productInStock);

        return productInStock;
    }

    public List<ProductInStock> checkReplenishment() {
        List<ProductInStock> productInStocks = productInStockRepository.findAll();
        return productInStocks.stream().filter(entry -> entry.getQuantity()<entry.getRecommendedQuantity())
                .collect(Collectors.toList());
    }

    @Transactional
    public void bookStock(RecipeCookingOrderDTO recipeCookingOrderDTO) {
        //check timestamp
        LocalDateTime timestamp = recipeCookingOrderDTO.getTimestamp();
        Optional<StockChange> stockChangeRepositoryByTimestamp = stockChangeRepository.findByTimestamp(timestamp);
        if (stockChangeRepositoryByTimestamp.isPresent()) throw new ProductStockAlreadyChanged("this recipe already book");


        //Get list of productId
        List<ProductAndQuantityDTO> bookOrder = recipeCookingOrderDTO.getProductAndQuantityDTOList();
        List<Integer> productIdList = bookOrder.stream()
                .map(ProductAndQuantityDTO::getProductId).collect(Collectors.toList());

        //create stock map to check
        List<ProductInStock> currentStock = productInStockRepository.findAllByProductIn(productIdList);
        Map<Integer, Integer> stockMap = new HashMap<>();
        currentStock.forEach(entry -> stockMap.put(entry.getProduct().getProductId(), entry.getQuantity()));

        //double check avaiability
        Optional<ProductAndQuantityDTO> impossibleProduct = bookOrder.stream()
                .filter(entry -> entry.getQuantity() > stockMap.getOrDefault(entry.getProductId(), 0))
                .findAny();
        if (impossibleProduct.isPresent()) throw new ProductNotEnoughQuantityException("Not enough to prepare");

        //create change map
        Map<Integer, Integer> changeMap = new HashMap<>();
        bookOrder.forEach(entry -> changeMap.put(entry.getProductId(), entry.getQuantity()));

        //set new quantity and save
        currentStock.forEach(entry ->
                entry.setQuantity(entry.getQuantity()-changeMap.get(entry.getProduct().getProductId())));
        currentStock.forEach(productInStockRepository::save);

        //register change
        bookOrder.forEach(entry ->
                stockChangeRepository.addStockChange(entry.getProductId(), -entry.getQuantity(), timestamp));

    }


}
