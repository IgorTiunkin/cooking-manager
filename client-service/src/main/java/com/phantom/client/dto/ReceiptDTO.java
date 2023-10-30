package com.phantom.client.dto;

import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptDTO {

    private Integer receiptId;

    private String title;

    private TreeMap<ProductDTO, Integer> usedProducts =
            new TreeMap<>(Comparator.comparing(ProductDTO::getProductName));//todo convert to linked map

    private String actions;

    @Override
    public String toString() {
        return "ReceiptDTO{" +
                "title='" + title + '\'' +
                ", usedProducts=" + usedProducts +
                ", actions='" + actions + '\'' +
                '}';
    }
}
