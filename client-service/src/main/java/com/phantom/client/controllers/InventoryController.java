package com.phantom.client.controllers;


import com.phantom.client.dto.ProductDTO;
import com.phantom.client.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{id}")
    public String getProductById(@PathVariable ("id") Integer productId,
                                 Model model) throws ExecutionException, InterruptedException {
        ProductDTO productDTO = inventoryService.getProductById(productId).get();
        log.info("Get product by id = {}", productDTO.getProductId());
        model.addAttribute("product", productDTO);
        return "inventory/product";
    }

}
