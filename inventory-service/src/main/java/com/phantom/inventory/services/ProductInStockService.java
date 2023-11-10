package com.phantom.inventory.services;

import com.phantom.inventory.models.ProductInStock;
import com.phantom.inventory.repositories.ProductInStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductInStockService {

    private final ProductInStockRepository productInStockRepository;

    public List <ProductInStock> getAllStockQuantityById(List<Integer> listOfProductId) {
        return productInStockRepository.findAllByProductIn(listOfProductId);
    }

    public Integer getQuantityById(Integer productId) {
        return productInStockRepository.getQuantityById(productId).orElse(0);
    }
}
