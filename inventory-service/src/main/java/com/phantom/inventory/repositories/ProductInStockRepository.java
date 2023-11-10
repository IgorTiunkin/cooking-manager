package com.phantom.inventory.repositories;

import com.phantom.inventory.models.ProductInStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInStockRepository extends JpaRepository <ProductInStock, Integer> {
    @Query("SELECT c FROM ProductInStock c WHERE c.stockId IN ?1")
    List<ProductInStock> findAllByProductIn(List <Integer> idList);

    @Query("SELECT c.quantity FROM ProductInStock c WHERE c.product.productId = ?1")
    Optional <Integer> getQuantityById(Integer productId);
}
