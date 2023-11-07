package com.phantom.client.controllers;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ProductToAdd;
import com.phantom.client.dto.RecipeDTO;
import com.phantom.client.exceptions.InventoryServiceException;
import com.phantom.client.exceptions.InventoryServiceTooManyRequestsException;
import com.phantom.client.services.RecipeService;
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
@RequestMapping("/recipe")
@SessionAttributes({"products", "blank"})
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final RecipeService recipeService;

    private static final String RECIPE_CREATE_VIEW = "recipe/create";
    private static final String RECIPE_ALL_VIEW = "recipe/all";
    private static final String INVENTORY_SERVICE_ERROR_VIEW = "recipe/errors/inventory_service_error";
    private static final String RECIPE_VIEW = "recipe/recipe";
    private static final String WELCOME_VIEW = "recipe/welcome";

    @GetMapping
    public String welcome() {
        return WELCOME_VIEW;
    }


    @GetMapping("/all")
    public String getAllRecipes(Model model) {
        List<RecipeDTO> allReceipts = recipeService.getAllRecipes();
        model.addAttribute("recipes", allReceipts);
        return RECIPE_ALL_VIEW;
    }


    @GetMapping("/recipe/{recipe-id}")
    public String getRecipe(@PathVariable("recipe-id") Integer recipeId, Model model) {
        RecipeDTO recipeById = recipeService.getRecipeById(recipeId);
        model.addAttribute("recipe", recipeById);
        return RECIPE_VIEW;
    }


    @GetMapping("/create")
    public String getCreationBlank(Model model) throws ExecutionException, InterruptedException {
        List<ProductDTO> allProducts = recipeService.getAllProducts().get();
        model.addAttribute("blank", new RecipeDTO());
        model.addAttribute("products", allProducts);
        model.addAttribute("newProduct", new ProductToAdd());
        return RECIPE_CREATE_VIEW;
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
                             @ModelAttribute ("blank") RecipeDTO recipeDTO,
                             @ModelAttribute ("products") List <ProductDTO> productDTOList,
                             Model model) {
        if (bindingResult.hasErrors()) {
            log.info("Wrong request on adding product. {}", bindingResult.getFieldErrors());
            return RECIPE_CREATE_VIEW;
        }
        Map<ProductDTO, Integer> usedProducts = recipeDTO.getUsedProducts();
        ProductDTO productToAddDTO = productDTOList.stream()
                .filter(productDTO -> productDTO.getProductId().equals(productToAdd.getProductId()))
                .findAny().get();
        usedProducts.put(productToAddDTO, productToAdd.getQuantity());
        if (productToAdd.getQuantity()==0) {
            usedProducts.remove(productToAddDTO);
        }
        return RECIPE_CREATE_VIEW;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ("blank") @Valid RecipeDTO recipeDTO,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()){
            log.info("Wrong request on adding receipt {}", bindingResult.getFieldErrors());
            model.addAttribute("newProduct", new ProductToAdd());
            return RECIPE_CREATE_VIEW;
        }
        recipeService.save(recipeDTO);
        log.info("Receipt successfully created. {}", recipeDTO);
        return "redirect:/recipe/all";
    }

}
