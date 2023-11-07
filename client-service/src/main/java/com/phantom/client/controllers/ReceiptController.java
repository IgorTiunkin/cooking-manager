package com.phantom.client.controllers;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ProductToAdd;
import com.phantom.client.dto.ReceiptDTO;
import com.phantom.client.exceptions.InventoryServiceException;
import com.phantom.client.exceptions.InventoryServiceTooManyRequestsException;
import com.phantom.client.services.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/receipts")
@SessionAttributes({"products", "blank"})
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;

    private static final String RECEIPT_CREATE_VIEW = "receipts/create";
    private static final String RECEIPT_ALL_VIEW = "receipts/all";
    private static final String INVENTORY_SERVICE_ERROR_VIEW = "receipts/errors/inventory_service_error";
    private static final String RECEIPT_VIEW = "receipts/receipt";

    @GetMapping("/all")
    public String getAllReceipts(Model model) {
        List<ReceiptDTO> allReceipts = receiptService.getAllReceipts();
        model.addAttribute("receipts", allReceipts);
        return RECEIPT_ALL_VIEW;
    }


    @GetMapping("/receipt/{receipt-id}")
    public String getReceipt (@PathVariable("receipt-id") Integer receiptId, Model model) {
        ReceiptDTO receiptById = receiptService.getReceiptById(receiptId);
        model.addAttribute("receipt", receiptById);
        return RECEIPT_VIEW;
    }


    @GetMapping("/create")
    public String getCreationBlank(Model model) throws ExecutionException, InterruptedException {
        List<ProductDTO> allProducts = receiptService.getAllProducts().get();
        model.addAttribute("blank", new ReceiptDTO());
        model.addAttribute("products", allProducts);
        model.addAttribute("newProduct", new ProductToAdd());
        return RECEIPT_CREATE_VIEW;
    }

    @ExceptionHandler ({InventoryServiceException.class, InventoryServiceTooManyRequestsException.class})
    public String failedGetProducts (Model model, RuntimeException exception){
        log.info("Get exception while calling Inventory service, {}", exception.getMessage());
        model.addAttribute("exceptionMessage", exception.getMessage());
        return INVENTORY_SERVICE_ERROR_VIEW;
    }


    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute ("newProduct") @Valid ProductToAdd productToAdd,
                             BindingResult bindingResult,
                             @ModelAttribute ("blank") ReceiptDTO receiptDTO,
                             @ModelAttribute ("products") List <ProductDTO> productDTOList,
                             Model model) {
        if (bindingResult.hasErrors()) {
            log.info("Wrong request on adding product. {}", bindingResult.getFieldErrors());
            return RECEIPT_CREATE_VIEW;
        }
        Map<ProductDTO, Integer> usedProducts = receiptDTO.getUsedProducts();
        ProductDTO productToAddDTO = productDTOList.stream()
                .filter(productDTO -> productDTO.getProductId().equals(productToAdd.getProductId()))
                .findAny().get();
        usedProducts.put(productToAddDTO, productToAdd.getQuantity());
        if (productToAdd.getQuantity()==0) {
            usedProducts.remove(productToAddDTO);
        }
        return RECEIPT_CREATE_VIEW;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ("blank") @Valid ReceiptDTO receiptDTO,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()){
            log.info("Wrong request on adding receipt {}", bindingResult.getFieldErrors());
            model.addAttribute("newProduct", new ProductToAdd());
            return RECEIPT_CREATE_VIEW;
        }
        receiptService.save(receiptDTO);
        log.info("Receipt successfully created. {}", receiptDTO);
        return "redirect:/receipts/all";
    }

}
