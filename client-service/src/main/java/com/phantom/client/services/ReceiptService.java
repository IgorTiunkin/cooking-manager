package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ProductToAdd;
import com.phantom.client.dto.ReceiptDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptService {

    public List<ReceiptDTO> getAllReceipts() {
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
        Map<ProductDTO, Integer> receiptDTOIntegerMap = new HashMap<>();
        receiptDTOIntegerMap.put(productDTO1, 2);
        receiptDTOIntegerMap.put(productDTO2, 3);
        ReceiptDTO receiptDTO = ReceiptDTO.builder()
                .receiptId(1)
                .title("Bread with water")
                .actions("Mix.Eat")
                .usedProducts(receiptDTOIntegerMap)
                .build();
        return List.of(receiptDTO, receiptDTO);//todo get from service
    }

    public ReceiptDTO getReceiptById(Integer receiptId) {
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
        Map<ProductDTO, Integer> receiptDTOIntegerMap = new HashMap<>();
        receiptDTOIntegerMap.put(productDTO1, 2);
        receiptDTOIntegerMap.put(productDTO2, 3);
        ReceiptDTO receiptDTO = ReceiptDTO.builder()
                .receiptId(1)
                .title("Bread with water")
                .actions("Mix.Eat")
                .usedProducts(receiptDTOIntegerMap)
                .build();
        return receiptDTO; //todo get from service
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

    public boolean save(ReceiptDTO receiptDTO) {
        return true;//todo save to service
    }
}
