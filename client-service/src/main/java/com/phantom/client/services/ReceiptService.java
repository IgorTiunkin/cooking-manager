package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ProductToAdd;
import com.phantom.client.dto.ReceiptDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ReceiptService {

    public List<ReceiptDTO> getAllReceipts() {
        return Collections.emptyList();//todo get from service
    }

    public ReceiptDTO getReceiptById(Integer receiptId) {
        return null; //todo get from service
    }

    public List <ProductDTO> getAllProducts() {
        ProductDTO productDTO1 = ProductDTO.builder()
                .productId(1)
                .productName("water")
                .calories(10)
                .build();
        ProductDTO productDTO2 = ProductDTO.builder()
                .productId(2)
                .productName("bread")
                .calories(200)
                .build();
        return List.of(productDTO1, productDTO2);//todo get from service
    }
}
