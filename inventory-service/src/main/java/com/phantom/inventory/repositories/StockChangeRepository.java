package com.phantom.inventory.repositories;

import com.phantom.inventory.models.StockChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StockChangeRepository extends JpaRepository<StockChange, Integer> {
    Optional<StockChange> findByTimestamp(LocalDateTime timestamp);
}
