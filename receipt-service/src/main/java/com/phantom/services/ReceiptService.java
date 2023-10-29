package com.phantom.services;

import com.phantom.dto.ReceiptDTO;
import com.phantom.models.Receipt;
import com.phantom.repositories.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;


    public List<Receipt> getAllReceipts() {
       return receiptRepository.findAll();
    }

    @Transactional
    public boolean save(Receipt receipt) {
        receiptRepository.save(receipt);
        return true;
    }

    public boolean delete(Receipt receipt) {
        receiptRepository.delete(receipt);
        return true;
    }

    public Receipt getReceiptById(int id) {
        return receiptRepository.findById(id).orElseThrow(RuntimeException::new);//todo custom exception
    }
}
