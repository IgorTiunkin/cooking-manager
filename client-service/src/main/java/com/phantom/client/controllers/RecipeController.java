package com.phantom.client.controllers;

import com.phantom.client.dto.*;
import com.phantom.client.exceptions.receiptservice.DeleteFailedException;
import com.phantom.client.exceptions.receiptservice.UpdateFailedException;
import com.phantom.client.exceptions.inventoryservice.resilence.InventoryServiceException;
import com.phantom.client.exceptions.inventoryservice.resilence.InventoryServiceTooManyRequestsException;
import com.phantom.client.exceptions.receiptservice.SaveFailedException;
import com.phantom.client.mappers.RecipeRestToDtoMapper;
import com.phantom.client.services.InventoryService;
import com.phantom.client.services.RecipeService;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/recipe")
@SessionAttributes({"products", "recipe"})
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;
    private final InventoryService inventoryService;
    private final RecipeRestToDtoMapper recipeRestToDtoMapper;

    private static final String RECIPE_CREATE_VIEW = "recipe/create";
    private static final String RECIPE_ALL_VIEW = "recipe/all";
    private static final String INVENTORY_SERVICE_ERROR_VIEW = "recipe/errors/inventory_service_error";
    private static final String RECIPE_VIEW = "recipe/recipe";
    private static final String WELCOME_VIEW = "recipe/welcome";
    private static final String RECIPE_DELETE_ERROR_VIEW = "recipe/errors/recipe_delete_error";
    private static final String RECIPE_EDIT_VIEW = "recipe/edit";

    @GetMapping
    public String welcome() {
        return WELCOME_VIEW;
    }


    @GetMapping("/all")
    public String getAllRecipes(Model model) throws ExecutionException, InterruptedException{//todo catch
        List<RecipeRestDTO> recipeRestDTOS = recipeService.getAllRecipes().get();
        List<RecipeShowDTO> recipeShowDTOS = recipeRestToDtoMapper.mapToRecipeShowDto(recipeRestDTOS);
        model.addAttribute("recipes", recipeShowDTOS);
        return RECIPE_ALL_VIEW;
    }


    @GetMapping("/recipe/{recipe-id}")
    public String getRecipe(@PathVariable("recipe-id") Integer recipeId, Model model) throws ExecutionException, InterruptedException {
        RecipeRestDTO recipeRestDTO = recipeService.getRecipeById(recipeId).get();
        RecipeShowDTO recipeShowDTO = recipeRestToDtoMapper.mapToRecipeShowDto(List.of(recipeRestDTO)).get(0);
        model.addAttribute("recipe", recipeShowDTO);
        return RECIPE_VIEW;
    }


    @GetMapping("/create")
    public String getCreationBlank(Model model) throws ExecutionException, InterruptedException {
        List<ProductDTO> allProducts = inventoryService.getAllProducts().get();
        model.addAttribute("recipe", new RecipeShowDTO());
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
    public String addProduct(@ModelAttribute("newProduct") @Valid ProductToAdd productToAdd,
                             BindingResult bindingResult,
                             @ModelAttribute("recipe") RecipeShowDTO recipeShowDTO,
                             @ModelAttribute("products") List<ProductDTO> productDTOList) {
        if (bindingResult.hasErrors()) {
            log.info("Wrong request on adding product. {}", bindingResult.getFieldErrors());
            if (recipeShowDTO.getRecipeId()==null) return RECIPE_CREATE_VIEW;
            return RECIPE_EDIT_VIEW;
        }
        Map<ProductDTO, Integer> usedProducts = recipeShowDTO.getUsedProducts();
        ProductDTO productToAddDTO = productDTOList.stream()
                .filter(productDTO -> productDTO.getProductId().equals(productToAdd.getProductId()))
                .findAny().get();
        usedProducts.put(productToAddDTO, productToAdd.getQuantity());
        if (productToAdd.getQuantity()==0) {
            usedProducts.remove(productToAddDTO);
        }
        if (recipeShowDTO.getRecipeId()==null) return RECIPE_CREATE_VIEW;
        return RECIPE_EDIT_VIEW;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ("recipe") @Valid RecipeShowDTO recipeShowDTO,
                       BindingResult bindingResult,
                       Model model) throws ExecutionException, InterruptedException {
        if (bindingResult.hasErrors()){
            log.info("Wrong request on adding receipt {}", bindingResult.getFieldErrors());
            model.addAttribute("newProduct", new ProductToAdd());
            return RECIPE_CREATE_VIEW;
        }
        RecipeRestDTO recipeRestDTO = recipeRestToDtoMapper.convertToRecipeRestDTO(recipeShowDTO);
        RecipeRestDTO savedRecipeRestDTO = recipeService.save(recipeRestDTO).get();
        log.info("Receipt successfully created. {}", savedRecipeRestDTO);
        return "redirect:/recipe/all";
    }

    @ExceptionHandler (SaveFailedException.class)
    public String failedSaveRecipe(Model model, SaveFailedException exception,
                                   WebRequest request, HttpSession session){
        log.info("Failed to save");
        RecipeShowDTO recipe = (RecipeShowDTO) session.getAttribute("recipe");
        List <ProductDTO> productDTOList = (List<ProductDTO>) session.getAttribute("products");
        model.addAttribute("recipe", recipe);
        model.addAttribute("products",productDTOList);
        model.addAttribute("exceptionMessage", exception.getMessage());
        model.addAttribute("newProduct", new ProductToAdd());
        return RECIPE_CREATE_VIEW;
    }

    @DeleteMapping ("/delete/{id}")
    public String deleteRecipe (@PathVariable("id") Integer recipeId) throws ExecutionException, InterruptedException {
        log.info("Delete request for recipe id {}", recipeId);
        RecipeRestDTO recipeRestDTO = recipeService.delete(recipeId).get();
        log.info("Recipe successfully deleted. Recipe id = {}", recipeRestDTO.getRecipeId());
        return "redirect:/recipe/all";
    }

    @ExceptionHandler(DeleteFailedException.class)
    public String failedDeleteRecipe(DeleteFailedException exception, Model model) {
        log.info("Recipe deletion failed.");
        model.addAttribute("exceptionMessage", exception.getMessage());
        return RECIPE_DELETE_ERROR_VIEW;
    }

    @GetMapping("/edit/{id}")
    public String editRecipe (@PathVariable ("id") Integer recipeId,
                              @ModelAttribute("recipe") RecipeShowDTO recipeShowDTO,
                              Model model) throws ExecutionException, InterruptedException {
        List<ProductDTO> allProducts = inventoryService.getAllProducts().get();
        model.addAttribute("products", allProducts);
        model.addAttribute("newProduct", new ProductToAdd());
        return RECIPE_EDIT_VIEW;
    }

    @PostMapping("/edit")
    public String saveEditRecipe (@ModelAttribute("recipe") @Valid RecipeShowDTO recipeShowDTO,
                                  BindingResult bindingResult, Model model) throws ExecutionException, InterruptedException {
        if (bindingResult.hasErrors()){
            log.info("Wrong request on updating receipt {}", bindingResult.getFieldErrors());
            model.addAttribute("newProduct", new ProductToAdd());
            return RECIPE_EDIT_VIEW;
        }
        RecipeRestDTO recipeRestDTO = recipeRestToDtoMapper.convertToRecipeRestDTO(recipeShowDTO);
        RecipeRestDTO updatedRecipeRestDTO = recipeService.update(recipeRestDTO).get();
        log.info("Receipt successfully created. {}", updatedRecipeRestDTO);
        return "redirect:/recipe/recipe/"+recipeRestDTO.getRecipeId();
    }


    @ExceptionHandler (UpdateFailedException.class)
    public String failedUpdateRecipe (Model model, UpdateFailedException exception,
                                     WebRequest request, HttpSession session){
        log.info("Failed to update");
        RecipeShowDTO recipeShowDTO = (RecipeShowDTO) session.getAttribute("recipe");
        List <ProductDTO> productDTOList = (List<ProductDTO>) session.getAttribute("products");
        model.addAttribute("recipe", recipeShowDTO);
        model.addAttribute("products",productDTOList);
        model.addAttribute("exceptionMessage", exception.getMessage());
        model.addAttribute("newProduct", new ProductToAdd());
        return RECIPE_EDIT_VIEW;
    }

    @GetMapping("/prepare/{id}")
    public String getPrePreparationInfo(@PathVariable ("id") Integer recipeId,
                                        @ModelAttribute("recipe") RecipeShowDTO recipeShowDTO,
                                        Model model) throws ExecutionException, InterruptedException {
        log.info("Request preparation info fpr recipe {}", recipeId);
        RecipeRestDTO recipeRestDTO = recipeRestToDtoMapper.convertToRecipeRestDTO(recipeShowDTO);
        List<ProductsForPrepareDTO> productsForPrepareDTOS = inventoryService.getStockForProducts(recipeRestDTO).get();
        productsForPrepareDTOS.forEach(productsForPrepareDTO ->
                productsForPrepareDTO.setNeededQuantity
                        (this.getNeededQuantityFromRecipe(recipeShowDTO, productsForPrepareDTO)));

        RecipePrepareDTO recipePrepareDTO = RecipePrepareDTO.builder()
                .recipeId(recipeShowDTO.getRecipeId())
                .title(recipeShowDTO.getTitle())
                .actions(recipeShowDTO.getActions())
                .productsForPrepareDTOS(productsForPrepareDTOS)
                .build();
        model.addAttribute("recipe_prepare", recipePrepareDTO);
        Optional<ProductsForPrepareDTO> impossibleProduct = recipePrepareDTO.getProductsForPrepareDTOS().stream()
                .filter(product -> product.getNeededQuantity() > product.getCurrentQuantity())
                .findAny();
        Boolean canPrepare = impossibleProduct.isEmpty();
        model.addAttribute("can_prepare", canPrepare);
        return "/recipe/preparation_info";
    }

    private Integer getNeededQuantityFromRecipe(RecipeShowDTO recipeShowDTO, ProductsForPrepareDTO productsForPrepareDTO) {
        Integer productId = productsForPrepareDTO.getProductId();
        return recipeShowDTO.getUsedProducts().entrySet().stream()
                .filter(entry -> entry.getKey().getProductId() == productId)
                .findAny().get().getValue();
    }



}
