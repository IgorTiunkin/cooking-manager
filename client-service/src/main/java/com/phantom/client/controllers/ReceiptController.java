package com.phantom.client.controllers;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ProductToAdd;
import com.phantom.client.dto.ReceiptDTO;
import com.phantom.client.exceptions.ProductsServiceNotAvailableException;
import com.phantom.client.services.ReceiptService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Controller
@RequestMapping("/receipts")
@SessionAttributes({"products", "blank"})
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping("/all")
    public String getAllReceipts(Model model) {
        List<ReceiptDTO> allReceipts = receiptService.getAllReceipts();
        model.addAttribute("receipts", allReceipts);
        return "receipts/all";
    }


    @GetMapping("/receipt/{receipt-id}")
    public String getReceipt (@PathVariable("receipt-id") Integer receiptId, Model model) {
        ReceiptDTO receiptById = receiptService.getReceiptById(receiptId);
        model.addAttribute("receipt", receiptById);
        return "receipts/receipt";
    }


    @GetMapping("/create")
    public String getCreationBlank(Model model) throws ExecutionException, InterruptedException {
        List<ProductDTO> allProducts = receiptService.getAllProducts().get();
        model.addAttribute("blank", new ReceiptDTO());
        model.addAttribute("products", allProducts);
        model.addAttribute("newProduct", new ProductToAdd());
        return "receipts/create";
    }

    @ExceptionHandler (ProductsServiceNotAvailableException.class)
    public String failedGetProducts (RuntimeException exception){
        log.info("Get exception while calling Inventory service, {}", exception.getMessage());
        return "receipts/errors/ProductsNotFound";
    }


    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute ("newProduct") ProductToAdd productToAdd,
                             @ModelAttribute ("blank") ReceiptDTO receiptDTO,
                             @ModelAttribute ("products") List <ProductDTO> productDTOList) {
        Map<ProductDTO, Integer> usedProducts = receiptDTO.getUsedProducts();
        ProductDTO productToAddDTO = productDTOList.stream()
                .filter(productDTO -> productDTO.getProductId() == productToAdd.getProductId())
                .findAny().get();
        usedProducts.put(productToAddDTO, productToAdd.getQuantity());
        if (productToAdd.getQuantity()==0) {
            usedProducts.remove(productToAddDTO);
        }
        return "receipts/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ("blank") ReceiptDTO receiptDTO) {
        System.out.println(receiptDTO);
        receiptService.save(receiptDTO);
        return "redirect:/receipts/all";
    }

}
