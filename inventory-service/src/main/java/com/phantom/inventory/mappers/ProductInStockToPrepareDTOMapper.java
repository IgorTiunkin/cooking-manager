package com.phantom.inventory.mappers;

import com.phantom.inventory.dto.ProductInStockDTO;
import com.phantom.inventory.dto.ProductsForPrepareDTO;
import com.phantom.inventory.models.ProductInStock;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProductInStockToPrepareDTOMapper {

    public ProductsForPrepareDTO convertToPrepareDto(ProductInStock productInStock) {
        return ProductsForPrepareDTO.builder()
                .productId(productInStock.getProduct().getProductId())
                .productName(productInStock.getProduct().getProductName())
                .currentQuantity(productInStock.getQuantity())
                .build();
    }
}
