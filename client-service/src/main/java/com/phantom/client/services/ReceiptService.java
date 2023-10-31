package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ProductToAdd;
import com.phantom.client.dto.ReceiptDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final WebClient.Builder builder;

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
        TreeMap<ProductDTO, Integer> receiptDTOIntegerMap = new TreeMap<>();
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
        TreeMap<ProductDTO, Integer> receiptDTOIntegerMap = new TreeMap<>();
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
        ProductDTO[] productDTOS = builder.build().get()
                .uri("http://api-gateway/api/v1/product/all")
                .retrieve()
                .bodyToMono(ProductDTO[].class)
                .block();
        List<ProductDTO> productDTOList = Arrays.stream(productDTOS).collect(Collectors.toList());
        productDTOList.sort(Comparator.comparing(ProductDTO::getProductName));
        return productDTOList;
    }


    public boolean save(ReceiptDTO receiptDTO) {
        return true;//todo save to service
    }
}
