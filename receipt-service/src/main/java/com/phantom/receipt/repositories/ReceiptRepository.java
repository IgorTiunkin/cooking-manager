package com.phantom.receipt.repositories;

import com.phantom.receipt.models.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository <Receipt, Integer> {
}
