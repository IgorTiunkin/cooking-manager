package com.phantom.client.controllers;


import com.phantom.client.dto.ProductDTO;
import com.phantom.client.exceptions.inventoryservice.ProductDeleteAbsentException;
import com.phantom.client.exceptions.inventoryservice.ProductSaveException;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes("product")
public class InventoryController {

    private final InventoryService inventoryService;

    private static final String INVENTORY_ALL_VIEW = "inventory/all";
    private static final String INVENTORY_PRODUCT_VIEW = "inventory/product";
    private static final String INVENTORY_CREATE_VIEW = "inventory/create";
    private static final String INVENTORY_EDIT_VIEW = "inventory/edit";
    private static final String PRODUCT_DELETE_ERROR_VIEW = "/inventory/errors/product_delete_error";

    @GetMapping("/all")
    public String getAllProduct(Model model) throws ExecutionException, InterruptedException {
        log.info("Requested all products");
        List<ProductDTO> productDTOS = inventoryService.getAllProducts().get();
        model.addAttribute("products", productDTOS);
        return INVENTORY_ALL_VIEW;
    }

    @GetMapping("/product/{id}")
    public String getProductById(@PathVariable ("id") Integer productId,
                                 Model model) throws ExecutionException, InterruptedException {
        ProductDTO productDTO = inventoryService.getProductById(productId).get();
        log.info("Get product by id = {}", productDTO.getProductId());
        model.addAttribute("product", productDTO);
        return INVENTORY_PRODUCT_VIEW;
    }

    @GetMapping("/create")
    public String getCreationBlank(Model model) {
        log.info("Requested creation blank");
        model.addAttribute("product", new ProductDTO());
        return INVENTORY_CREATE_VIEW;
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute ("product") @Valid ProductDTO productDTO,
                              BindingResult bindingResult) throws ExecutionException, InterruptedException {
        log.info("Request save product. Name {}", productDTO.getProductName());
        if (bindingResult.hasErrors()) {
            return INVENTORY_CREATE_VIEW;
        }
        ProductDTO savedProductDTO = inventoryService.save(productDTO).get();
        log.info("Product successfully saved. Id {}", savedProductDTO.getProductId());
        return "redirect:/inventory/all";
    }

    @ExceptionHandler (ProductSaveException.class)
    public String failedUpdate(WebRequest request, HttpSession session,
                               ProductSaveException productSaveException,
                               Model model) {
        ProductDTO productDTO = (ProductDTO) session.getAttribute("product");
        log.info("Failed save product. Name {}", productDTO.getProductName());
        model.addAttribute("product", productDTO);
        model.addAttribute("exceptionMessage", productSaveException.getMessage());
        return INVENTORY_CREATE_VIEW;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable ("id") Integer productId) throws ExecutionException, InterruptedException {
        log.info("Request delete product. Id {}", productId);
        ProductDTO productDTO = inventoryService.delete(productId).get();
        log.info("Product deleted. Id {}",productDTO.getProductId());
        return "redirect:/inventory/all";
    }

    @ExceptionHandler (ProductDeleteAbsentException.class)
    public String failedDeleteProduct(ProductDeleteAbsentException productDeleteAbsentException, Model model) {
        log.info("Failed delete product, {}", productDeleteAbsentException.getMessage());
        model.addAttribute("exceptionMessage", productDeleteAbsentException.getMessage());
        return PRODUCT_DELETE_ERROR_VIEW;
    }


    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable ("id") Integer productId,
                              @ModelAttribute ("product") ProductDTO productDTO,
                              Model model) {
        log.info("Request for update product. Name {}", productDTO.getProductName());
        model.addAttribute("product", productDTO);
        return INVENTORY_EDIT_VIEW;
    }

    @PostMapping("/edit")
    public String saveEditProduct(@ModelAttribute ("product") @Valid ProductDTO productDTO,
                                  BindingResult bindingResult) throws ExecutionException, InterruptedException {
        log.info("Edit product request. Id = {}", productDTO.getProductId());
        if (bindingResult.hasErrors()) {
            return INVENTORY_EDIT_VIEW;
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
        return INVENTORY_EDIT_VIEW;
    }

}
