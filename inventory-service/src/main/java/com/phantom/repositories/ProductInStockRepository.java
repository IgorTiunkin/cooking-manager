package com.phantom.repositories;

import com.phantom.models.ProductInStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInStockRepository extends JpaRepository <ProductInStock, Integer> {
    @Query("SELECT c FROM ProductInStock c WHERE c.stockId IN ?1")
    List<ProductInStock> findAllByProductIn(List <Integer> idList);
}
