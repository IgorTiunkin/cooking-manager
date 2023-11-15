package com.phantom.inventory.repositories;

import com.phantom.inventory.models.StockChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockChangeRepository extends JpaRepository<StockChange, Integer> {

    List <StockChange> findAllByTimestamp(LocalDateTime timestamp);

    @Modifying
    @Transactional
    @Query (value = "insert into stock_change (product_id, change, timestamp) VALUES (?1, ?2, ?3)",
            nativeQuery = true)
    int addStockChange(Integer productId, Integer change, LocalDateTime timestamp);
}
