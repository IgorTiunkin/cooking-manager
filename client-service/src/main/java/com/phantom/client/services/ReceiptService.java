package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ReceiptDTO;
import com.phantom.client.exceptions.ProductsServiceNotAvailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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



    @CircuitBreaker(name = "inventory")
    @Retry(name = "inventory", fallbackMethod = "failedGetProducts")
    public List <ProductDTO> getAllProducts(){
        return builder.build().get()
                .uri("http://api-gateway/api/v1/product/all")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectSortedList(Comparator.comparing(ProductDTO::getProductName))
                .block();
    }

    public List <ProductDTO> failedGetProducts (RuntimeException e) {
        throw new ProductsServiceNotAvailableException("Service is unavailable");
    }


    public boolean save(ReceiptDTO receiptDTO) {
        return true;//todo save to service
    }
}
