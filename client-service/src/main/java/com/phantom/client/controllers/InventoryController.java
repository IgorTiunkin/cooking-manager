package com.phantom.client.controllers;


import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.RecipeShowDTO;
import com.phantom.client.exceptions.inventoryservice.ProductUpdateException;
import com.phantom.client.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes("product")
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

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable ("id") Integer productId,
                              @ModelAttribute ("product") ProductDTO productDTO,
                              Model model) {
        log.info("Request for update product. Name {}", productDTO.getProductName());
        model.addAttribute("product", productDTO);
        return "inventory/edit";
    }

    @PostMapping("/edit")
    public String saveEditProduct(@ModelAttribute ("product") @Valid ProductDTO productDTO,
                                  BindingResult bindingResult) throws ExecutionException, InterruptedException {
        log.info("Edit product request. Id = {}", productDTO.getProductId());
        if (bindingResult.hasErrors()) {
            return "inventory/edit";
        }
        ProductDTO editProduct = inventoryService.updateProduct(productDTO).get();
        log.info("Product successfully edit. Id = {}", editProduct.getProductId());
        return "redirect:/inventory/product/" + productDTO.getProductId();
    }

    @ExceptionHandler (ProductUpdateException.class)
    public String failedUpdate(WebRequest request, HttpSession session,
                               ProductUpdateException productUpdateException,
                               Model model) {
        ProductDTO productDTO = (ProductDTO) session.getAttribute("product");
        log.info("Failed update product. Id {}", productDTO.getProductId());
        model.addAttribute("product", productDTO);
        model.addAttribute("exceptionMessage", productUpdateException.getMessage());
        return "inventory/edit";
    }

}
